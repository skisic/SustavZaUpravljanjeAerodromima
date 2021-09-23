package org.foi.nwtis.skisic.zrna;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.rest.klijenti.LIQKlijent;
import org.foi.nwtis.rest.klijenti.OWMKlijent;
import org.foi.nwtis.rest.podaci.Lokacija;
import org.foi.nwtis.skisic.eb.Airports;
import org.foi.nwtis.skisic.eb.Myairports;
import org.foi.nwtis.skisic.eb.Myairportslog;
import org.foi.nwtis.skisic.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.skisic.sb.AirportsFacadeLocal;
import org.foi.nwtis.skisic.sb.MyairportsFacadeLocal;
import org.foi.nwtis.skisic.sb.MyairportslogFacadeLocal;

@Named(value = "pregledAerodroma")
@ViewScoped
public class PregledAerodroma implements Serializable {

    @EJB(beanName = "MyairportsFacade")
    MyairportsFacadeLocal myairportsFacade;

    @EJB(beanName = "MyairportslogFacade")
    MyairportslogFacadeLocal myairportslogFacade;

    @EJB(beanName = "AirportsFacade")
    AirportsFacadeLocal airportsFacade;

    @Inject
    ServletContext context;

    @Inject
    Prijava prijava;

    @Inject
    Lokalizacija lokalizacija;

    @Getter
    String korisnik;

    @Getter
    @Setter
    int brojKorisnika;

    @Getter
    @Setter
    int brojDanaLetova;

    @Getter
    @Setter
    int brojPreuzetihLetova;

    @Getter
    String poruka;

    @Getter
    @Setter
    List<Airports> prikaziAerodrome;

    @Getter
    @Setter
    List<Airports> dajAerodromeNaziv;

    @Getter
    @Setter
    String sirina;

    @Getter
    @Setter
    String duzina;

    @Getter
    @Setter
    float temperatura;

    @Getter
    @Setter
    float vlaga;

    @Getter
    @Setter
    String ident;

    @Getter
    @Setter
    String naziv = "";

    @Getter
    @Setter
    List<Airports> mojiAerodromix = new ArrayList<>();

    List<Airports> aerodromi = new ArrayList<>();
    
    List<Myairports> listaAerodroma = new ArrayList<>();

    public PregledAerodroma() {
    }

    /**
     * Funkcija koja dohvaća liste aerodoroma i aerodroma korisnika
     */
    @PostConstruct
    public void dohvatiListe() {
        aerodromi = airportsFacade.findAll();
        listaAerodroma = myairportsFacade.findAll();
    }

    /**
     * Funkcija koja pribavlja podatke o prijavljenom korisniku
     */
    private void pribaviPodatke() {
        korisnik = prijava.getKorisnik();
    }

    /**
     * Prikazuje aerodrome
     * @return 
     */
    public List<Airports> prikaziAerodrome() {
        pribaviPodatke();
        List<Airports> mojiAerodromi = new ArrayList<>();
        for (Myairports ma : listaAerodroma) {
            for (Airports a : aerodromi) {
                if (ma.getIdent().getIdent().equals(a.getIdent()) && ma.getUsername().getKorIme().equals(korisnik)) {
                    mojiAerodromi.add(a);
                }
            }
        }
        return mojiAerodromi;
    }

    /**
     * Dohvaća broj korisnika koji prate određeni aerodrom
     * @param ident
     * @return 
     */
    public int pribaviBrojKorisnika(String ident) {
        brojKorisnika = 0;
        List<Myairports> listaAerodroma = myairportsFacade.findAll();
        for (Myairports ma : listaAerodroma) {
            if (ma.getIdent().getIdent().equals(ident)) {
                brojKorisnika++;
            }
        }
        return brojKorisnika;
    }

    /**
     * Dohvaća broj dana prikupljenih letova
     * @param ident
     * @return 
     */
    public int pribaviBrojDanaLetova(String ident) {
        brojDanaLetova = 0;
        List<Myairportslog> listaLog = myairportslogFacade.findAll();
        for (Myairportslog mal : listaLog) {
            if (mal.getMyairportslogPK().getIdent().equals(ident)) {
                brojDanaLetova++;
            }
        }
        return brojDanaLetova;
    }

    /**
     * Dohvaća broj preuzetih letova aerodroma
     * @param ident
     * @return 
     */
    public int pribaviBrojPreuzetihLetovaAerodroma(String ident) {
        brojPreuzetihLetova = 0;
        List<Myairportslog> listaLog = myairportslogFacade.findAll();
        for (Myairportslog mal : listaLog) {
            if (mal.getMyairportslogPK().getIdent().equals(ident)) {
                brojPreuzetihLetova = brojPreuzetihLetova + mal.getNumberofflights();
            }
        }
        return brojPreuzetihLetova;

    }

    /**
     * Funkcija koja preuzima GPS i meteo podatke sa servisa LocationIQ i
     * OpenWeatherMap
     *
     * @param ident
     */
    public void preuzmiGPSMeteoPodatke(String ident) {
        BP_Konfiguracija konf = (BP_Konfiguracija) context.getAttribute("BP_Konfig");
        LIQKlijent liq = new LIQKlijent(konf.getKonfig().dajPostavku("LocationIQ.token"));
        sirina = liq.getGeoLocation(ident).getLatitude();
        duzina = liq.getGeoLocation(ident).getLongitude();

        OWMKlijent owm = new OWMKlijent(konf.getKonfig().dajPostavku("OpenWeatherMap.apikey"));
        temperatura = owm.getRealTimeWeather(sirina, duzina).getTemperatureValue();
        vlaga = owm.getRealTimeWeather(sirina, duzina).getHumidityValue();
    }

    public List<Airports> dajAerodromeNaziv() {
        List<Airports> lista = new ArrayList<>();
        if (naziv.isEmpty()) {
            lista = aerodromi;
        } else {
            for (Airports a : aerodromi) {
                if (a.getName().contains(naziv)) {
                    lista.add(a);
                }
            }
        }
        return lista;
    }
}
