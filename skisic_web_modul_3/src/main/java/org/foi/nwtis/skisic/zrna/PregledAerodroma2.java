package org.foi.nwtis.skisic.zrna;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Response;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.podaci.OdgovorAerodrom;
import org.foi.nwtis.skisic.rest.klijenti.ProjektRS_2;
import org.foi.nwtis.skisic.ws.klijenti.ProjektWS_1;
import org.foi.nwtis.skisic.ws.serveri.Aerodrom;

@Named(value = "pregledAerodroma2")
@ViewScoped
public class PregledAerodroma2 implements Serializable {

    @Inject
    ServletContext context;

    @Inject
    Prijava prijava;

    @Inject
    Lokalizacija lokalizacija;

    @Getter
    String korisnik;

    @Getter
    String lozinka;

    @Getter
    @Setter
    String ident;

    @Getter
    @Setter
    String min = "0";

    @Getter
    @Setter
    String max = "1000";

    @Getter
    @Setter
    String udaljenost = "0";

    @Getter
    @Setter
    String icao1 = "";
    @Getter
    @Setter
    String icao2 = "";

    @Getter
    @Setter
    List<org.foi.nwtis.podaci.Aerodrom> mojiAerodromi = new ArrayList<>();

    @Getter
    @Setter
    List<Aerodrom> aerodromiGranica = new ArrayList<>();

    @Getter
    String poruka = "";

    public PregledAerodroma2() {
    }
    
    /**
     * Funkcija koja dohvaća aerodrome korisnika putem rest servisa
     * @return 
     */
    public List<org.foi.nwtis.podaci.Aerodrom> dajAerodromeKorisnikaRS() {
        preuzmiPodatkeKorisnika();
        ProjektRS_2 rs = new ProjektRS_2(korisnik, lozinka);
        OdgovorAerodrom odgovor = rs.dajAerodomeKorisnika(OdgovorAerodrom.class);
        mojiAerodromi = Arrays.asList(odgovor.getOdgovor());
        return mojiAerodromi;
    }

    /**
     * Funkcija za preuzimanje podataka o prijavljenom korisniku
     */
    public void preuzmiPodatkeKorisnika() {
        korisnik = prijava.getKorisnik();
        lozinka = prijava.getLozinka();
    }

    /**
     * Funkcija koja daje aerodrome koji se nalaze unutar određenih granica
     */
    public void dajAerodromeGranica() {
        preuzmiPodatkeKorisnika();
        ProjektWS_1 ws = new ProjektWS_1();
        aerodromiGranica = ws.dajAerodromeGranica(korisnik, lozinka, ident, Integer.parseInt(min), Integer.parseInt(max));

    }

    /**
     * Funkcija koja daje udaljenost između dva aerodroma
     */
    public void dajAerodromeUdaljenost() {
        preuzmiPodatkeKorisnika();
        ProjektWS_1 ws = new ProjektWS_1();
        udaljenost = String.valueOf(ws.dajUdaljenostAerodroma(korisnik, lozinka, icao1, icao2));
    }

    /**
     * Funkcija koja briše aerodrom korisnika
     */
    public void obrisiAerodromKorisnika() {
        ResourceBundle res = ResourceBundle.getBundle("org.foi.nwtis.skisic.web.Prijevod", new Locale(lokalizacija.getJezik()));
        ProjektRS_2 rs = new ProjektRS_2(korisnik, lozinka);
        if (ident != null || !ident.equals("")) {
            System.out.println("Brišem aerodrom!" + ident);
            Response odgovor = rs.obrisiAerodomKorisnika(ident);
            String result = odgovor.readEntity(String.class);
            if (result.equals("{\"poruka\":\"OK\",\"status\":\"10\"}")) {
                poruka = res.getString("pregledAerodroma2.brisanjeOK");
                System.out.println("Aerodrom obrisan!");
            } else {
                poruka = res.getString("pregledAerodroma2.brisanjeERR");
                System.out.println("Aerodrom nije obrisan!");
            }
        }
    }
    
    /**
     * Funkcija koja briše letove aviona
     */
    public void obrisiLetoveAviona(){
        ResourceBundle res = ResourceBundle.getBundle("org.foi.nwtis.skisic.web.Prijevod", new Locale(lokalizacija.getJezik()));
        ProjektRS_2 rs = new ProjektRS_2(korisnik, lozinka);
        if (ident != null || !ident.equals("")) {
            System.out.println("Brišem letove..." + ident);
            Response odgovor = rs.obrisiAvioneAerodroma(ident);
            String result = odgovor.readEntity(String.class);
            if (result.equals("{\"poruka\":\"OK\",\"status\":\"10\"}")) {
                poruka = res.getString("pregledAerodroma2.brisanjeOKa");
                System.out.println("Letovi obrisani!");
            } else {
                poruka = res.getString("pregledAerodroma2.brisanjeERRa");
                System.out.println("Letovi nisu obrisani!");
            }
        }
    }

}
