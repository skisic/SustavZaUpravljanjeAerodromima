package org.foi.nwtis.skisic.ejb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.inject.Inject;
import org.foi.nwtis.rest.klijenti.OSKlijent;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.skisic.eb.Airplanes;
import org.foi.nwtis.skisic.eb.Myairports;
import org.foi.nwtis.skisic.eb.Myairportslog;
import org.foi.nwtis.skisic.eb.MyairportslogPK;
import org.foi.nwtis.skisic.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.skisic.sb.AirplanesFacadeLocal;
import org.foi.nwtis.skisic.sb.AirportsFacadeLocal;
import org.foi.nwtis.skisic.sb.KorisniciFacadeLocal;
import org.foi.nwtis.skisic.sb.MyairportsFacadeLocal;
import org.foi.nwtis.skisic.sb.MyairportslogFacadeLocal;

@Singleton
public class PreuzimanjeAviona implements PreuzimanjeAvionaLocal {

    @EJB(beanName = "KorisniciFacade")
    KorisniciFacadeLocal korisniciFacade;

    @EJB(beanName = "MyairportsFacade")
    MyairportsFacadeLocal myairportsFacade;

    @EJB(beanName = "MyairportslogFacade")
    MyairportslogFacadeLocal myairportslogFacade;

    @EJB(beanName = "AirportsFacade")
    AirportsFacadeLocal airportsFacade;

    @EJB(beanName = "AirplanesFacade")
    AirplanesFacadeLocal airplanesFacade;
    private String tekst = "";

    public BP_Konfiguracija bpk;
    boolean krajPreuzimanja = false;
    private long odVremena;
    private long doVremena;
    private String osnKorisnik;
    private String osnLozinka;
    private boolean status;
    private int ciklus;
    private int pauza;
    private String pocetak;
    private String kraj;
    private int port;

    /**
     * Funkcija koja pribavlja pocetno vrijeme preuzimanja i dan nakon te ih pretvara u UNIX timestamp
     * @param datum - datum pocetnog preuzimanja u formatu dd.MM.yyyy
     * @throws ParseException 
     */
    private void pribaviVrijeme(String datum) throws ParseException {
        Date od_;
        Date do_;
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd.MM.yyyy");
        Calendar c = Calendar.getInstance();
        od_ = sdf1.parse(datum);
        odVremena = od_.getTime() / 1000L;
        c.setTime(od_);
        c.add(Calendar.DATE, 1);
        do_ = c.getTime();
        doVremena = do_.getTime() / 1000L;
    }

    /**
     * Funkcija koja pretvara datum iz oblika dd.MM.yyyy u yyyy-MM-dd
     * @param datum - datum oblika dd.MM.yyyy
     * @return
     * @throws ParseException 
     */
    private String pretvoriDatum(String datum) throws ParseException {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        Date dat = df.parse(datum);
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
        datum = df1.format(dat);
        return datum;
    }

    /**
     * Funkcija koja pretvara datuma formata dd.MM.yyyy u UNIX timestamp
     * @param datum
     * @return
     * @throws ParseException 
     */
    private String pretvoriDatumLong(long datum) throws ParseException {
        Date dat = new Date(datum * 1000);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        String datum1 = df.format(dat);
        return datum1;
    }

    /**
     * Provjerava postoji li zapis o preuzetom aerodromu u tablici myairportslog
     * @param lista lista svih zapisa u logu
     * @param ident ident aerodroma
     * @param datum datum preuzimanja letova
     * @return true ako postoji zapis, inace false
     * @throws ParseException 
     */
    private boolean postojiAerodromLog(List<Myairportslog> lista, String ident, Date datum) throws ParseException {
        boolean postoji = false;
        for (Myairportslog m : lista) {
            if (m.getAirports().getIdent().equals(ident)
                    && m.getMyairportslogPK().getFlightdate().equals(datum)) {
                postoji = true;
            }
        }
        return postoji;
    }

    /**
     * Funkcija za dodavanje aviona u bazu podataka
     * @param listaAviona popis preuzetih aviona
     * @param vrijeme vrijeme preuzimanja aviona
     */
    public void dodajAvion(List<AvionLeti> listaAviona, java.sql.Date vrijeme) {
        for (AvionLeti a : listaAviona) {
            Airplanes avion = new Airplanes();
            avion.setIcao24(a.getIcao24());
            if (a.getEstArrivalAirport() != null) {
                avion.setEstarrivalairport(a.getEstArrivalAirport());
                avion.setEstdepartureairport(a.getEstDepartureAirport());
                avion.setEstarrivalairporthorizdistance(a.getEstArrivalAirportHorizDistance());
                avion.setEstarrivalairportvertdistance(a.getEstArrivalAirportVertDistance());
                avion.setEstdepartureairporthorizdistance(a.getEstDepartureAirportHorizDistance());
                avion.setEstdepartureairportvertdistance(a.getEstDepartureAirportVertDistance());
                avion.setCallsign(a.getCallsign());
                avion.setArrivalairportcandidatescount(a.getArrivalAirportCandidatesCount());
                avion.setDepartureairportcandidatescount(a.getDepartureAirportCandidatesCount());
                avion.setFirstseen(a.getFirstSeen());
                avion.setLastseen(a.getLastSeen());
                avion.setStored(vrijeme);
                airplanesFacade.create(avion);
            }
        }
    }

    /**
     * Funkcija za dodavanje log zapisa u tablicu s brojem preuzetih letova
     * @param ident
     * @param vrijemeLeta
     * @param vrijemeSpremanja
     * @param brojLetova 
     */
    public void dodajLog(String ident, java.sql.Date vrijemeLeta, java.sql.Date vrijemeSpremanja, int brojLetova) {
        Myairportslog log = new Myairportslog(new MyairportslogPK(ident, vrijemeLeta), vrijemeSpremanja);
        log.setNumberofflights(brojLetova);
        myairportslogFacade.create(log);
    }

    /**
     * Funkcija za primanje poruke od servera
     *
     * @param socket - socket servera
     * @throws IOException
     */
    public void primiPoruku(Socket socket) throws IOException {
        InputStream is = socket.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        tekst = br.readLine();
        System.out.println(tekst);
        socket.shutdownInput();
    }

    /**
     * Funkcija za slanje poruke serveru
     *
     * @param socket - socket servera
     * @param poruka - sadržaj poruke
     * @throws IOException
     */
    public void posaljiPoruku(Socket socket, String poruka) throws IOException {
        OutputStream outs = socket.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(outs);
        osw.write(poruka);
        osw.flush();
        socket.shutdownOutput();
    }

    /**
     * Funkcija koja provjerava trenutno stanje preuzimanja na mrežnoj utičnici
     * @param port port socket servera
     * @return 
     */
    public boolean provjeriTrenutnoStanje(int port) {
        String korisnik = "pero";
        String lozinka = "123456";
        try {
            Socket s = new Socket("localhost", port);
            tekst = "KORISNIK " + korisnik + "; LOZINKA " + lozinka + "; STANJE;";
            posaljiPoruku(s, tekst);
            primiPoruku(s);
            String t = tekst.split(";")[0];
            if (t.split(" ")[1].equals("11")) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println("Server je ugašen!");
            return false;
        }
    }
    
    @Override
    @Asynchronous
    public void preuzmiAvione(BP_Konfiguracija bpk) {
        int brojac = 0;
        List<Myairports> aerodromiKorisnika = myairportsFacade.findAll();
        List<Myairportslog> lista = myairportslogFacade.findAll();
        Date danas = new Date();
        java.sql.Date trenutnoVrijeme = new java.sql.Date(danas.getTime());

        osnKorisnik = bpk.getKonfig().dajPostavku("OpenSkyNetwork.korisnik");
        osnLozinka = bpk.getKonfig().dajPostavku("OpenSkyNetwork.lozinka");
        status = Boolean.parseBoolean(bpk.getKonfig().dajPostavku("preuzimanje.status"));
        ciklus = Integer.parseInt(bpk.getKonfig().dajPostavku("preuzimanje.ciklus"));
        pauza = Integer.parseInt(bpk.getKonfig().dajPostavku("preuzimanje.pauza"));
        pocetak = bpk.getKonfig().dajPostavku("preuzimanje.pocetak");
        kraj = bpk.getKonfig().dajPostavku("preuzimanje.kraj");
        port = Integer.parseInt(bpk.getKonfig().dajPostavku("port"));
        
        System.out.println("KONFIGURACIJA:: " + osnKorisnik + " "+ osnLozinka + " "+ status + " "+ ciklus + " "+ pocetak + " "+ kraj);
        OSKlijent osKlijent = new OSKlijent(osnKorisnik, osnLozinka);
        try {
            pribaviVrijeme(pocetak);
        } catch (ParseException ex) {
            ex.getMessage();
        }
        while (status) {
            try {
                System.out.println("Brojac: " + brojac++);
                java.sql.Date vrijemeLeta = new java.sql.Date(odVremena * 1000);
                if(provjeriTrenutnoStanje(port)){
                for (Myairports ma : aerodromiKorisnika) {
                    if (!postojiAerodromLog(lista, ma.getIdent().getIdent(), vrijemeLeta)) {
                        List<AvionLeti> listaAviona = osKlijent.getDepartures(ma.getIdent().getIdent(), odVremena, doVremena);
                        new Thread() {
                            @Override
                            public void run() {
                                dodajAvion(listaAviona, trenutnoVrijeme);
                                dodajLog(ma.getIdent().getIdent(), vrijemeLeta, trenutnoVrijeme, listaAviona.size());
                            }
                        }.start();
                        Thread.sleep(pauza);
                        System.out.println("Broj letova: " + listaAviona.size() + " za " + ma.getIdent().getIdent());
                    }
                }
                System.out.println("Ciklus - čekanje...");
                Thread.sleep(ciklus * 1000);
                pocetak = pretvoriDatumLong(doVremena);
                System.out.println("Plus 1 dan...");
                pribaviVrijeme(pocetak);
                if (pocetak.equals(kraj)) {
                    System.out.println("Preuzimanje završeno! Spavanje 1 dan...");
                    Thread.sleep(86400000);
                }
                } else {
                    System.out.println("Preuzimanje pauzirano");
                    System.out.println("Ciklus - čekanje...");
                    Thread.sleep(ciklus * 1000);
                }
                
            } catch (ParseException | InterruptedException ex) {
                Logger.getLogger(PreuzimanjeAviona.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
