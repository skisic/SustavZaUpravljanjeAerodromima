package org.foi.nwtis.skisic.ws.klijenti;

import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.skisic.ws.serveri.Aerodrom;

/**
 * Klasa koja služi kao klijent za referencirani web servis
 * @author Sara
 */
public class ProjektWS_1 {
    
    /**
     * Daje aerodrome koje prati korisnik
     * @param korisnik
     * @param lozinka
     * @return 
     */
    public List<Aerodrom> dajAerodromeKorisnika(String korisnik, String lozinka) {
        List<Aerodrom> aerodromi = new ArrayList<>();
        try {
            org.foi.nwtis.skisic.ws.serveri.ProjektWS_Service service = new org.foi.nwtis.skisic.ws.serveri.ProjektWS_Service();
            org.foi.nwtis.skisic.ws.serveri.ProjektWS port = service.getProjektWSPort();
            aerodromi = port.dajAerodromeKorisnika(korisnik, lozinka);
        } catch (Exception ex) {
            ex.getMessage();
        }
        return aerodromi;
    }

    /**
     * Daje informacije o aerodromu kojeg prati korisnik
     * @param korisnik
     * @param lozinka
     * @param icao
     * @return 
     */
    public List<Aerodrom> dajAerodromKorisnika(String korisnik, String lozinka, String icao) {
        List<Aerodrom> aerodromi = new ArrayList<>();
        List<Aerodrom> aerodrom = new ArrayList<>();
        try {
            org.foi.nwtis.skisic.ws.serveri.ProjektWS_Service service = new org.foi.nwtis.skisic.ws.serveri.ProjektWS_Service();
            org.foi.nwtis.skisic.ws.serveri.ProjektWS port = service.getProjektWSPort();

            aerodromi = port.dajAerodromeKorisnika(korisnik, lozinka);
            for (Aerodrom a : aerodromi) {
                if (a.getIcao().equals(icao)) {
                    aerodrom.add(a);
                }
            }
        } catch (Exception ex) {
            ex.getMessage();
        }
        return aerodrom;
    }

    /**
     * Daje sve aerodrome koji sadrže neku riječ u nazivu
     * @param korisnik
     * @param lozinka
     * @param naziv
     * @return 
     */
    public List<Aerodrom> dajAerodromeNaziv(String korisnik, String lozinka, String naziv) {
        List<Aerodrom> aerodromi = new ArrayList<>();
        try {
            org.foi.nwtis.skisic.ws.serveri.ProjektWS_Service service = new org.foi.nwtis.skisic.ws.serveri.ProjektWS_Service();
            org.foi.nwtis.skisic.ws.serveri.ProjektWS port = service.getProjektWSPort();
            aerodromi = port.dajAerodromeNaziv(korisnik, lozinka, naziv);
        } catch (Exception ex) {
            ex.getMessage();
        }
        return aerodromi;
    }

    /**
     * Daje sve aerodrome koji sadrže odabranu riječ u državi
     * @param korisnik
     * @param lozinka
     * @param drzava
     * @return 
     */
    public List<Aerodrom> dajAerodromeDrzava(String korisnik, String lozinka, String drzava) {
        List<Aerodrom> aerodromi = new ArrayList<>();
        try {
            org.foi.nwtis.skisic.ws.serveri.ProjektWS_Service service = new org.foi.nwtis.skisic.ws.serveri.ProjektWS_Service();
            org.foi.nwtis.skisic.ws.serveri.ProjektWS port = service.getProjektWSPort();
            aerodromi = port.dajAerodromeDrzava(korisnik, lozinka, drzava);
        } catch (Exception ex) {
            ex.getMessage();
        }
        return aerodromi;
    }
    
   
}
