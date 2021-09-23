package org.foi.nwtis.skisic.podaci;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.foi.nwtis.skisic.eb.Airplanes;
import org.foi.nwtis.skisic.eb.Dnevnikrada;
import org.foi.nwtis.skisic.eb.Korisnici;
import org.foi.nwtis.skisic.eb.Myairports;
import org.foi.nwtis.skisic.eb.Myairportslog;
import org.foi.nwtis.skisic.sb.AirplanesFacadeLocal;
import org.foi.nwtis.skisic.sb.AirportsFacadeLocal;
import org.foi.nwtis.skisic.sb.DnevnikradaFacadeLocal;
import org.foi.nwtis.skisic.sb.KorisniciFacadeLocal;
import org.foi.nwtis.skisic.sb.MyairportsFacadeLocal;
import org.foi.nwtis.skisic.sb.MyairportslogFacadeLocal;

/**
 * Klasa za upravljanje radom s aerodromima
 * @author Sara
 */
@Named("upravljanjeAerodromima")
@SessionScoped
public class UpravljanjeAerodromima implements Serializable {

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

    List<Myairports> mojiAerodromi;
    List<Myairportslog> aerodromiLog;
    List<Korisnici> korisnici;
    List<Airplanes> avioni;

    /**
     * Funkcija za dohvaćanje liste korisnika prilikom učitavanja modula
     */
    @PostConstruct
    public void dohvatiListe() {
        korisnici = korisniciFacade.findAll();
    }

    /**
     * Funkcija za upisivanje zahtjeva web servisa u dnevnik rada u bazi
     * podataka
     *
     * @param korisnik - korisničko ime
     * @param zahtjev - naziv funkcije i njenih parametara koju korisnik obavlja
     */
    public void upisiUDnevnik(String korisnik, String zahtjev) {
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
     * @param korisnik korisničko ime
     * @param lozinka lozinka
     * @return true ako je autentikacija prošla
     */
    public boolean autentikacija(String korisnik, String lozinka) {
        for (Korisnici k : korisnici) {
            if (k.getKorIme().equals(korisnik) && k.getLozinka().equals(lozinka)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Funkcija za brisanje aerodroma ukolikko on ne sadrži nikakve letove
     * @param korisnik
     * @param lozinka
     * @param icao icao kod aerodroma
     * @return 
     */
    public boolean obrisiAerodrom(String korisnik, String lozinka, String icao) {
        mojiAerodromi = myairportsFacade.findAll();
        int brojLetova = airplanesFacade.dajLetoveAvionaAerodrom(icao).size();
        boolean obrisano = false;
        if (autentikacija(korisnik, lozinka)) {
            for (Myairports ma : mojiAerodromi) {
                if (ma.getUsername().getKorIme().equals(korisnik) && brojLetova == 0 && ma.getIdent().getIdent().equals(icao)) {
                    myairportsFacade.remove(ma);
                    obrisano = true;
                    break;
                }
            }
        }
        return obrisano;
    }

    /**
     * Funkcija koja provjerava prati li korisnik odabrani aerodrom
     * @param korisnik
     * @param icao icao kod aerodroma
     * @return 
     */
    public boolean imaAerodrom(String korisnik, String icao) {
        mojiAerodromi = myairportsFacade.findAll();
        for (Myairports ma : mojiAerodromi) {
            if (ma.getUsername().getKorIme().equals(korisnik) && ma.getIdent().getIdent().equals(icao)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Funkcija za brisanje svih letova (aviona) određenog aerodroma
     * @param korisnik
     * @param lozinka
     * @param icao icao kod aerodroma
     * @return 
     */
    public boolean obrisiAvioneAerodroma(String korisnik, String lozinka, String icao) {
        avioni = airplanesFacade.dajLetoveAvionaAerodrom(icao);
        boolean obrisano = false;
        if (autentikacija(korisnik, lozinka)) {
            if (imaAerodrom(korisnik, icao)) {
                for (Airplanes a : avioni) {
                    airplanesFacade.remove(a);
                }
                obrisano = true;
            }
        }
        return obrisano;
    }
}
