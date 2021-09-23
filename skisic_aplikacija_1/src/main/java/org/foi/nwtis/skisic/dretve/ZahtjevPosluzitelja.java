package org.foi.nwtis.skisic.dretve;

import org.foi.nwtis.skisic.podaci.KorisnikDAO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.ParseException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.dkermek.ws.serveri.Aerodrom;
import org.foi.nwtis.skisic.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.skisic.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.skisic.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.skisic.ws.klijenti.AerodromiWS_1;

public class ZahtjevPosluzitelja extends Thread {

    public String parametar = "";
    public String parametarGrupe = "";
    public Socket s;
    public String tekst;
    private String svnKorisnik;
    private String svnLozinka;
    private String aerodromi = "";
    KorisnikDAO k = null;
    BP_Konfiguracija bpk;
    AerodromiWS_1 ws = new AerodromiWS_1();

    public ZahtjevPosluzitelja(Socket s, BP_Konfiguracija bpk) {
        this.s = s;
        this.bpk = bpk;
    }
    
    /**
     * Funkcija za prekidanje rada dretve
     */
    @Override
    public void interrupt() {
        super.interrupt();
    }

    /**
     * Funkcija za pokretanje dretve koja prima poruku i analizira ju
     */
    @Override
    public void run() {
        try {
            primiPoruku(s);
            analizaZahtjeva();
        } catch (IOException | ParseException | NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            Logger.getLogger(ZahtjevPosluzitelja.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Funkcija za početnu inicijalizaciju dretve
     */
    @Override
    public synchronized void start() {
        super.start();
    }

    /**
     * Funkcija za učitavanje konfiguracije korisničkog imena i lozinke
     */
    public void ucitajKonfiguraciju() {
        svnKorisnik = bpk.getKonfig().dajPostavku("svn.username");
        svnLozinka = bpk.getKonfig().dajPostavku("svn.password");
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
     * Funkcija koja analizira zahtjeve korisnika i prema tome šalje serveru
     * aviona poruku potvrde ili grešku ovisno o postojanju aviona i letova
     *
     * @throws IOException
     * @throws ParseException
     * @throws org.foi.nwtis.skisic.konfiguracije.NemaKonfiguracije
     * @throws org.foi.nwtis.skisic.konfiguracije.NeispravnaKonfiguracija
     */
    public void analizaZahtjeva() throws IOException, ParseException, NemaKonfiguracije, NeispravnaKonfiguracija {
        k = new KorisnikDAO();
        String[] p = tekst.split(";");
        String korisnik = p[0].split(" ")[1];
        String lozinka = p[1].split(" ")[2];

        if (p.length >= 3) {
            parametar = p[2].trim();
            if (parametar.contains("GRUPA")) {
                if(parametar.contains("AERODROMI")){
                    aerodromi = parametar;
                }
                parametarGrupe = parametar.split(" ")[1];
            }
            parametar = parametar.split(" ")[0];
        }

        if (parametar.isEmpty()) {
            provjeraKorisnika(korisnik, lozinka);

        } else if (parametar.equals("DODAJ")) {
            dodajKorisnika(korisnik, lozinka);

        } else if (parametar.equals("GRUPA") && parametarGrupe.equals("PRIJAVI")) {
            registrirajGrupu();

        } else if (k.provjeraKorisnika(korisnik, lozinka, bpk)) {

            switch (parametar) {
                case "PAUZA":
                    promijeniStatusFalse();
                    break;
                case "RADI":
                    promijeniStatusTrue();
                    break;
                case "KRAJ":
                    zavrsiRad();
                    break;
                case "STANJE":
                    provjeriStanje();
                    break;
                case "GRUPA":
                    analizaZahtjevaGrupe();
                    break;
                default:
                    posaljiPoruku(s, "Pogrešno upisani parametri!");
                    break;
            }
        } else {
            posaljiPoruku(s, "Pogreška!");
        }
    }

    /**
     * Funkcija koja uzima parametre iz komande za grupu
     * @throws IOException 
     */
    public void analizaZahtjevaGrupe() throws IOException {
        switch (parametarGrupe) {
            case "ODJAVI":
                deregistrirajGrupu();
                break;
            case "AKTIVIRAJ":
                aktivirajGrupu();
                break;
            case "BLOKIRAJ":
                blokirajGrupu();
                break;
            case "STANJE":
                dajStanjeGrupe();
                break;
            case "AERODROMI":
                dodajAerodromeGrupi(aerodromi);
            default:
                
                break;
        }
    }

    /**
     * FUnkcija za autentikaciju korisnika koja šalje poruku s rezultatom autentikacije
     * @param korisnik korisničko ime   
     * @param lozinka lozinka
     * @return
     * @throws IOException 
     */
    private boolean provjeraKorisnika(String korisnik, String lozinka) throws IOException {
        if (k.provjeraKorisnika(korisnik, lozinka, bpk)) {
            posaljiPoruku(s, "OK 10;");
            return true;
        } else {
            posaljiPoruku(s, "ERR 11;");
            korisnik = "";
            lozinka = "";
            return false;
        }
    }

    /**
     * Funkcija koja provjerava postoji li korisničko ime u bazi podataka
     * @param korisnik
     * @return 
     */
    private boolean postojiKorisnik(String korisnik) {
        boolean postoji = false;
        List<String> korime = k.dohvatiImena(bpk);
        for (String s : korime) {
            if (s.equals(korisnik)) {
                postoji = true;
            }
        }
        return postoji;
    }

    /**
     * Funkcija za dodavanje korisnika prilikom registracije, šalje poruku korisniku s rezultatom dodavanja
     * @param korisnik
     * @param lozinka
     * @throws IOException 
     */
    private void dodajKorisnika(String korisnik, String lozinka) throws IOException {
        if (!postojiKorisnik(korisnik)) {
            k.dodajKorisnika(korisnik, lozinka, bpk);
            posaljiPoruku(s, "OK 10;");
        } else {
            posaljiPoruku(s, "ERR 11;");
        }
    }

    /**
     * Funkcija za promjenu statusa preuzimanja na false, tj. pauziranje preuzimanja
     * Šalje poruku korisniku s rezultatom funkcije
     * @throws IOException
     * @throws NemaKonfiguracije
     * @throws NeispravnaKonfiguracija 
     */
    private void promijeniStatusFalse() throws IOException, NemaKonfiguracije, NeispravnaKonfiguracija {
        if (bpk.getKonfig().dajPostavku("preuzimanje.status").equals("true")) {
            bpk.getKonfig().azurirajPostavku("preuzimanje.status", "false");
            bpk.getKonfig().spremiKonfiguraciju();
            posaljiPoruku(s, "OK 10;");
        } else {
            posaljiPoruku(s, "ERR 12;");
        }
    }

    /**
     * FUnkcija za nastavak preuzimanja aviona, postavlja stanje preuzimanja na true
     * Šalje korisniku poruku s rezultatom obrade funkcije
     * @throws NemaKonfiguracije
     * @throws IOException
     * @throws NeispravnaKonfiguracija 
     */
    private void promijeniStatusTrue() throws NemaKonfiguracije, IOException, NeispravnaKonfiguracija {
        if (bpk.getKonfig().dajPostavku("preuzimanje.status").equals("false")) {
            bpk.getKonfig().azurirajPostavku("preuzimanje.status", "true");
            bpk.getKonfig().spremiKonfiguraciju();
            posaljiPoruku(s, "OK 10;");
        } else {
            posaljiPoruku(s, "ERR 13;");
        }
    }

    /**
     * Funkcija za zaustavljanje rada mrežne utičnice
     * @throws NeispravnaKonfiguracija
     * @throws IOException
     * @throws NemaKonfiguracije 
     */
    private void zavrsiRad() throws NeispravnaKonfiguracija, IOException, NemaKonfiguracije {
        bpk.getKonfig().azurirajPostavku("preuzimanje.status", "true");
        bpk.getKonfig().spremiKonfiguraciju();
        posaljiPoruku(s, "OK 10;");
        s.shutdownInput();
    }

    /**
     * Funkcija za provjeru trenutnog stanja preuzimanja
     * Postavlja dd na 11 ako je preuzimanje aktivno ili 12 ako je preuzimanje pauzirano
     * @throws IOException 
     */
    private void provjeriStanje() throws IOException {
        int dd;
        if (bpk.getKonfig().dajPostavku("preuzimanje.status").equals("true")) {
            dd = 11;
        } else {
            dd = 12;
        }
        posaljiPoruku(s, "OK " + dd + ";");
    }

    /**
     * Funkcija za registraciju grupe, provjerava status grupe prije registracije
     * Ovisno o statusu šalje odgovor klijentu
     * @throws IOException 
     */
    private void registrirajGrupu() throws IOException {
        ucitajKonfiguraciju();
        if (ws.provjeriStatusGrupe(svnKorisnik, svnLozinka).equals("NEPOSTOJI") ||
                ws.provjeriStatusGrupe(svnKorisnik, svnLozinka).equals("DEREGISTRIRAN")) {
            ws.registrirajGrupu(svnKorisnik, svnLozinka);
            posaljiPoruku(s, "OK 20;");
        } else {
            posaljiPoruku(s, "ERR 20;");
        }
    }

    /**
     * Funkcija za deregistraciju ili odjavu grupe
     * Ovisno o statusu grupe šalje poruku klijentu
     * @throws IOException 
     */
    private void deregistrirajGrupu() throws IOException {
        ucitajKonfiguraciju();
        if (ws.provjeriStatusGrupe(svnKorisnik, svnLozinka).equals("REGISTRIRAN")) {
            ws.deregistrirajGrupu(svnKorisnik, svnLozinka);
            posaljiPoruku(s, "OK 20;");
        } else {
            posaljiPoruku(s, "ERR 21;");
        }
    }

    /**
     * Funkcija za aktivaciju grupe
     * Ovisno o statusu grupe šalje odgovor klijentu
     * @throws IOException 
     */
    private void aktivirajGrupu() throws IOException {
        ucitajKonfiguraciju();
        if (ws.provjeriStatusGrupe(svnKorisnik, svnLozinka).equals("NEAKTIVAN") || 
                ws.provjeriStatusGrupe(svnKorisnik, svnLozinka).equals("REGISTRIRAN") ||  
                ws.provjeriStatusGrupe(svnKorisnik, svnLozinka).equals("BLOKIRAN")) {
            ws.aktivirajGrupu(svnKorisnik, svnLozinka);
            posaljiPoruku(s, "OK 20;");
        } else if (ws.provjeriStatusGrupe(svnKorisnik, svnLozinka).equals("AKTIVAN")) {
            posaljiPoruku(s, "ERR 22;");
        } else if (ws.provjeriStatusGrupe(svnKorisnik, svnLozinka).equals("NEPOSTOJI")) {
            posaljiPoruku(s, "ERR 21;");
        }
    }

    /**
     * Funkcija koja blokira grupu
     * Ovisno o statusu grupe šalje odgovor klijentu
     * @throws IOException 
     */
    private void blokirajGrupu() throws IOException {
        ucitajKonfiguraciju();
        if (ws.provjeriStatusGrupe(svnKorisnik, svnLozinka).equals("AKTIVAN")) {
            ws.blokirajGrupu(svnKorisnik, svnLozinka);
            posaljiPoruku(s, "OK 20;");
        } else if (ws.provjeriStatusGrupe(svnKorisnik, svnLozinka).equals("NEAKTIVAN")) {
            posaljiPoruku(s, "ERR 23;");
        } else if (ws.provjeriStatusGrupe(svnKorisnik, svnLozinka).equals("NEPOSTOJI")) {
            posaljiPoruku(s, "ERR 21;");
        }
    }

    /**
     * Funkcija koja daje trenutno stanje grupe i prema danom stanju šalje poruku klijentu
     * @throws IOException 
     */
    private void dajStanjeGrupe() throws IOException {
        ucitajKonfiguraciju();
        System.out.println("Status grupe: " + ws.provjeriStatusGrupe(svnKorisnik, svnLozinka));
        switch (ws.provjeriStatusGrupe(svnKorisnik, svnLozinka)) {
            case "AKTIVAN":
                posaljiPoruku(s, "OK 21;");
                break;
            case "BLOKIRAN":
                posaljiPoruku(s, "OK 22;");
                break;
            case "NEPOSTOJI":
                posaljiPoruku(s, "ERR 21;");
                break;
            default:
                posaljiPoruku(s, ws.provjeriStatusGrupe(svnKorisnik, svnLozinka));
                break;
        }
    }
    
    /**
     * FUnkcija za dodavanje aerodroma grupi ukoliko je status grupe blokiran
     * @param icaoLista
     * @throws IOException 
     */
    private void dodajAerodromeGrupi(String icaoLista) throws IOException{
        ucitajKonfiguraciju();
        if(ws.provjeriStatusGrupe(svnKorisnik, svnLozinka).equals("BLOKIRAN")){
            ws.obrisiAerodromeGrupe(svnKorisnik, svnLozinka);
            String[] lista = icaoLista.split(" ");
            int n = lista.length;
            String icao = "";
            for(int i = 2; i < n-1; i++){
                icao = lista[i].split(",")[0];
                ws.dodajAerodromIcaoGrupi(svnKorisnik, svnLozinka, icao);
            }
            icao = lista[n-1];
            ws.dodajAerodromIcaoGrupi(svnKorisnik, svnLozinka, icao);
            posaljiPoruku(s, "OK 20;");
        }
    }
}
