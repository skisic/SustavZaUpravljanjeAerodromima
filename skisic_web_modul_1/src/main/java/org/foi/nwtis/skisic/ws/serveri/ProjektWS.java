package org.foi.nwtis.skisic.ws.serveri;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.servlet.ServletContext;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.Lokacija;
import org.foi.nwtis.skisic.UdaljenostAerodroma;
import org.foi.nwtis.skisic.eb.Airplanes;
import org.foi.nwtis.skisic.eb.Airports;
import org.foi.nwtis.skisic.eb.Dnevnikrada;
import org.foi.nwtis.skisic.eb.Korisnici;
import org.foi.nwtis.skisic.eb.Myairports;
import org.foi.nwtis.skisic.sb.AirplanesFacadeLocal;
import org.foi.nwtis.skisic.sb.AirportsFacadeLocal;
import org.foi.nwtis.skisic.sb.DnevnikradaFacadeLocal;
import org.foi.nwtis.skisic.sb.KorisniciFacadeLocal;
import org.foi.nwtis.skisic.sb.MyairportsFacadeLocal;
import org.foi.nwtis.skisic.sb.MyairportslogFacadeLocal;

/**
 * Klasa JAX-WS web servisa koja poziva njegove funkcije
 * @author Sara
 */

@WebService(serviceName = "ProjektWS")
public class ProjektWS {

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

    @EJB(beanName = "DnevnikradaFacade")
    DnevnikradaFacadeLocal dnevnikradaFacade;

    /**
     * Funkcija za upisivanje zahtjeva web servisa u dnevnik rada u bazi
     * podataka
     *
     * @param korisnik - korisničko ime
     * @param zahtjev - naziv funkcije i njenih parametara koju korisnik obavlja
     */
    private void upisiUDnevnik(String korisnik, String zahtjev) {
        Date danas = new Date();
        java.sql.Date trenutnoVrijeme = new java.sql.Date(danas.getTime());
        Dnevnikrada dr = new Dnevnikrada();
        dr.setKorisnik(korisnik);
        dr.setZahtjev(zahtjev);
        dr.setVrijeme(trenutnoVrijeme);
        dnevnikradaFacade.create(dr);
    }

    /**
     * Funkcija za autentikaciju korisnika
     *
     * @param korisnik
     * @param lozinka
     * @return true ako su korisničko ime i lozinka valjani, inače false
     */
    @WebMethod(operationName = "provjeraKorisnika")
    public boolean provjeraKorisnika(@WebParam(name = "korisnik") String korisnik,
            @WebParam(name = "lozinka") String lozinka) {
        String zahtjev = "Autentikacija korisnika";
        upisiUDnevnik(korisnik, zahtjev);
        List<Korisnici> listaKorisnika = korisniciFacade.findAll();
        for (Korisnici k : listaKorisnika) {
            if (k.getKorIme().equals(korisnik) && k.getLozinka().equals(lozinka)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Funkcija koja vraća popis aerodroma koji sadrže upisanu riječ u svojem
     * nazivu
     *
     * @param korisnik
     * @param lozinka
     * @param naziv - naziv aerodroma
     * @return popis aerodroma
     */
    @WebMethod(operationName = "dajAerodromeNaziv")
    public List<Aerodrom> dajAerodromeNaziv(@WebParam(name = "korisnik") String korisnik,
            @WebParam(name = "lozinka") String lozinka, @WebParam(name = "naziv") String naziv) {
        String zahtjev = "Dohvaca popis aerodroma s nazivom " + naziv;
        upisiUDnevnik(korisnik, zahtjev);
        List<Airports> listaAerodroma = airportsFacade.findAll();
        List<Aerodrom> aerodrom = new ArrayList<>();
        String lokacija;
        if (provjeraKorisnika(korisnik, lozinka)) {
            for (Airports a : listaAerodroma) {
                if (a.getName().contains(naziv)) {
                    Aerodrom ae = new Aerodrom();
                    ae.setIcao(a.getIdent());
                    ae.setNaziv(a.getName());
                    ae.setDrzava(a.getIsoCountry());
                    lokacija = a.getCoordinates();
                    ae.setLokacija(new Lokacija(lokacija.split(",")[1], lokacija.split(",")[0].trim()));
                    aerodrom.add(ae);
                }
            }
            return aerodrom;
        }
        return null;
    }
    
    /**
     * Funkcija koja vraća popis aerodroma koji sadrže upisanu riječ u svojem
     * nazivu države
     *
     * @param korisnik
     * @param lozinka
     * @param drzava - drzava aerodroma
     * @return popis aerodroma
     */
    @WebMethod(operationName = "dajAerodromeDrzava")
    public List<Aerodrom> dajAerodromeDrzava(@WebParam(name = "korisnik") String korisnik,
            @WebParam(name = "lozinka") String lozinka, @WebParam(name = "drzava") String drzava) {
        String zahtjev = "Dohvaca popis aerodroma s drzavom " + drzava;
        upisiUDnevnik(korisnik, zahtjev);
        List<Airports> listaAerodroma = airportsFacade.findAll();
        List<Aerodrom> aerodrom = new ArrayList<>();
        String lokacija;
        if (provjeraKorisnika(korisnik, lozinka)) {
            for (Airports a : listaAerodroma) {
                if (a.getIsoCountry() != null && a.getIsoCountry().equals(drzava)) {
                    Aerodrom ae = new Aerodrom();
                    ae.setIcao(a.getIdent());
                    ae.setNaziv(a.getName());
                    ae.setDrzava(a.getIsoCountry());
                    lokacija = a.getCoordinates();
                    ae.setLokacija(new Lokacija(lokacija.split(",")[1], lokacija.split(",")[0].trim()));
                    aerodrom.add(ae);
                }
            }
            return aerodrom;
        }
        return null;
    }



    /**
     * Funkcija koja vraća popis aerodroma koje prati određeni korisnik
     *
     * @param korisnik
     * @param lozinka
     * @return popis aerodroma
     */
    @WebMethod(operationName = "dajAerodromeKorisnika")
    public List<Aerodrom> dajAerodromeKorisnika(@WebParam(name = "korisnik") String korisnik,
            @WebParam(name = "lozinka") String lozinka) {
        String zahtjev = "Dohvaca aerodrome koje prati korisnik";
        upisiUDnevnik(korisnik, zahtjev);
        List<Myairports> listaAerodromaKorisnika = myairportsFacade.findAll();
        List<Aerodrom> aerodrom = new ArrayList<>();
        String lokacija;
        if (provjeraKorisnika(korisnik, lozinka)) {
                for (Myairports ma : listaAerodromaKorisnika) {
                    if (ma.getUsername().getKorIme().equals(korisnik)) {
                        Aerodrom ae = new Aerodrom();
                        ae.setIcao(ma.getIdent().getIdent());
                        ae.setNaziv(ma.getIdent().getName());
                        ae.setDrzava(ma.getIdent().getIsoCountry());
                        lokacija = ma.getIdent().getCoordinates();
                        ae.setLokacija(new Lokacija(lokacija.split(",")[0], lokacija.split(",")[1].trim()));
                        aerodrom.add(ae);
                    }
                
            }
            return aerodrom;
        }
        return null;
    }
    
    /**
     * Funkcija koja vraća popis svih letova aviona koji polijeću s odabranog aerodroma
     *
     * @param korisnik
     * @param lozinka
     * @param icao
     * @param odVremena
     * @param doVremena
     * @return popis aviona
     */
    @WebMethod(operationName = "dajLetoveAvionaAerodrom")
    public List<AvionLeti> dajLetoveAvionaAerodrom(@WebParam(name = "korisnik") String korisnik,
            @WebParam(name = "lozinka") String lozinka,
            @WebParam(name = "icao") String icao,
            @WebParam(name = "odVremena") long odVremena,
            @WebParam(name = "doVremena") long doVremena) {
        String zahtjev = "Dohvaca letove aviona koji polijecu s " + icao + " u razdoblju od " + odVremena + " do " + doVremena;
        upisiUDnevnik(korisnik, zahtjev);
        List<Airplanes> listaAviona = airplanesFacade.dajLetoveAvionaAerodrom(icao);
        List<AvionLeti> avion = new ArrayList<>();
        if (provjeraKorisnika(korisnik, lozinka)) {
            for (Airplanes a : listaAviona) {
                if (a.getFirstseen() >= odVremena && a.getLastseen() <= doVremena) {
                    AvionLeti al = new AvionLeti();
                    al.setIcao24(a.getIcao24());
                    al.setFirstSeen(a.getFirstseen());
                    al.setLastSeen(a.getLastseen());
                    al.setCallsign(a.getCallsign());
                    al.setEstDepartureAirport(a.getEstdepartureairport());
                    al.setEstArrivalAirport(a.getEstarrivalairport());
                    al.setEstArrivalAirportHorizDistance(a.getEstarrivalairporthorizdistance());
                    al.setEstArrivalAirportVertDistance(a.getEstarrivalairportvertdistance());
                    al.setEstDepartureAirportHorizDistance(a.getEstdepartureairporthorizdistance());
                    al.setEstDepartureAirportVertDistance(a.getEstdepartureairportvertdistance());
                    al.setDepartureAirportCandidatesCount(a.getDepartureairportcandidatescount());
                    al.setArrivalAirportCandidatesCount(a.getArrivalairportcandidatescount());
                    avion.add(al);
                }
            }
            return avion;
        }
        return null;
    }

    /**
     * Funkcija koja vraća popis svih letova odabranog aviona
     *
     * @param korisnik
     * @param lozinka
     * @param icao
     * @param odVremena
     * @param doVremena
     * @return popis aerodroma
     */
    @WebMethod(operationName = "dajLetoveAviona")
    public List<AvionLeti> dajLetoveAviona(@WebParam(name = "korisnik") String korisnik,
            @WebParam(name = "lozinka") String lozinka,
            @WebParam(name = "icao") String icao,
            @WebParam(name = "odVremena") long odVremena,
            @WebParam(name = "doVremena") long doVremena) {
        String zahtjev = "Dohvaca letove aviona " + icao + " u razdoblju od " + odVremena + " do " + doVremena;
        upisiUDnevnik(korisnik, zahtjev);
        List<Airplanes> listaAviona = airplanesFacade.dajLetoveAviona(icao);
        List<AvionLeti> avion = new ArrayList<>();
        if (provjeraKorisnika(korisnik, lozinka)) {
            for (Airplanes a : listaAviona) {
                if (a.getFirstseen() >= odVremena && a.getLastseen() <= doVremena) {
                    AvionLeti al = new AvionLeti();
                    al.setIcao24(a.getIcao24());
                    al.setFirstSeen(a.getFirstseen());
                    al.setLastSeen(a.getLastseen());
                    al.setCallsign(a.getCallsign());
                    al.setEstDepartureAirport(a.getEstdepartureairport());
                    al.setEstArrivalAirport(a.getEstarrivalairport());
                    al.setEstArrivalAirportHorizDistance(a.getEstarrivalairporthorizdistance());
                    al.setEstArrivalAirportVertDistance(a.getEstarrivalairportvertdistance());
                    al.setEstDepartureAirportHorizDistance(a.getEstdepartureairporthorizdistance());
                    al.setEstDepartureAirportVertDistance(a.getEstdepartureairportvertdistance());
                    al.setDepartureAirportCandidatesCount(a.getDepartureairportcandidatescount());
                    al.setArrivalAirportCandidatesCount(a.getArrivalairportcandidatescount());
                    avion.add(al);
                }
            }
            return avion;
        }
        return null;
    }

    /**
     * Funkcija koja vraća udaljenost između dva upisana aerodroma
     *
     * @param korisnik
     * @param lozinka
     * @param icao1
     * @param icao2
     * @return popis aerodroma
     */
    @WebMethod(operationName = "dajUdaljenostAerodroma")
    public double dajUdaljenostAerodroma(@WebParam(name = "korisnik") String korisnik,
            @WebParam(name = "lozinka") String lozinka, @WebParam(name = "icao1") String icao1,
            @WebParam(name = "icao2") String icao2) {
        String zahtjev = "Ispis udaljenosti izmedu aerodroma " + icao1 + " i " + icao2;
        upisiUDnevnik(korisnik, zahtjev);
        List<Airports> listaAerodroma = airportsFacade.findAll();
        String lokacija;
        double long1 = 0;
        double long2 = 0;
        double lat1 = 0;
        double lat2 = 0;
        double udaljenost;
        Lokacija l = new Lokacija();
        UdaljenostAerodroma ua = new UdaljenostAerodroma();
        if (provjeraKorisnika(korisnik, lozinka)) {
            for (Airports a : listaAerodroma) {
                if (a.getIdent().equals(icao1)) {
                    lokacija = a.getCoordinates();
                    l = new Lokacija(lokacija.split(",")[1], lokacija.split(",")[0].trim());
                    long1 = Double.parseDouble(l.getLongitude());
                    lat1 = Double.parseDouble(l.getLatitude());
                    System.out.println("ICAO 1: " + long1 + " " + lat1);
                }
                if (a.getIdent().equals(icao2)) {
                    lokacija = a.getCoordinates();
                    l = new Lokacija(lokacija.split(",")[1], lokacija.split(",")[0].trim());
                    long2 = Double.parseDouble(l.getLongitude());
                    lat2 = Double.parseDouble(l.getLatitude());
                    System.out.println("ICAO 2: " + long2 + " " + lat2);
                }
            }

            udaljenost = ua.izracunajUdaljenost(lat1, long1, lat2, long2, 'K');
            return udaljenost;
        }
        return 0;
    }

    /**
     * Funkcija koja vraća popis svih aerodroma koji se nalaze unutar određene
     * granice
     *
     * @param korisnik
     * @param lozinka
     * @param icao
     * @param granicaOd
     * @param granicaDo
     * @return popis aerodroma
     */
    @WebMethod(operationName = "dajAerodromeGranica")
    public List<Aerodrom> dajAerodromeGranica(@WebParam(name = "korisnik") String korisnik,
            @WebParam(name = "lozinka") String lozinka,
            @WebParam(name = "icao") String icao,
            @WebParam(name = "granicaOd") int granicaOd,
            @WebParam(name = "granicaDo") int granicaDo) {
        String zahtjev = "Dohvaca aerodrome koji su od " + icao + " udaljeni izmedu " + granicaOd + " i " + granicaDo + " km";
        upisiUDnevnik(korisnik, zahtjev);
        List<Airports> listaAerodroma = airportsFacade.findAll();
        List<Myairports> mojiAerodromi = myairportsFacade.findAll();
        List<Aerodrom> aerodromiKorisnika = new ArrayList<>();
        String lokacija;
        Lokacija l = new Lokacija();
        double lat1 = 0;
        double long1 = 0;
        double lat2 = 0;
        double long2 = 0;
        double udaljenost;

        UdaljenostAerodroma u = new UdaljenostAerodroma();
        if (provjeraKorisnika(korisnik, lozinka)) {
            for (Airports a : listaAerodroma) {
                if (a.getIdent().equals(icao)) {
                    lokacija = a.getCoordinates();
                    l = new Lokacija(lokacija.split(",")[1], lokacija.split(",")[0].trim());
                    lat1 = Double.parseDouble(l.getLatitude());
                    long1 = Double.parseDouble(l.getLongitude());
                }
            }

            for (Myairports ma : mojiAerodromi) {
                for (Airports a : listaAerodroma) {
                    if (ma.getUsername().getKorIme().equals(korisnik) && ma.getIdent().getIdent().equals(a.getIdent())) {
                        lokacija = a.getCoordinates();
                        l = new Lokacija(lokacija.split(",")[1], lokacija.split(",")[0].trim());
                        lat2 = Double.parseDouble(l.getLatitude());
                        long2 = Double.parseDouble(l.getLongitude());
                        udaljenost = u.izracunajUdaljenost(lat1, long1, lat2, long2, 'K');
                        if (udaljenost <= granicaDo && udaljenost >= granicaOd) {
                            Aerodrom ae = new Aerodrom();
                            ae.setIcao(a.getIdent());
                            ae.setDrzava(a.getIsoCountry());
                            ae.setNaziv(a.getName());
                            ae.setLokacija(new Lokacija(lokacija.split(",")[1], lokacija.split(",")[0].trim()));
                            aerodromiKorisnika.add(ae);
                        }

                    }
                }
            }
            return aerodromiKorisnika;
        }
        return null;
    }
}
