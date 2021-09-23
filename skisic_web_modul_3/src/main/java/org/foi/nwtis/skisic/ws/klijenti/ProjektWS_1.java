package org.foi.nwtis.skisic.ws.klijenti;

import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.skisic.ws.serveri.Aerodrom;
import org.foi.nwtis.skisic.ws.serveri.AvionLeti;

/**
 * Klasa koja služi kao klijent za web servis JAX-WS
 * @author Sara
 */
public class ProjektWS_1 {

    public ProjektWS_1() {
    }
    
    /**
     * Funkcija koja dohvaća aerodorme koje prati korisnik
     * @param korisnik
     * @param lozinka
     * @return 
     */
    public List<Aerodrom> dajAerodromeKorisnika(String korisnik, String lozinka){
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
     * Funkcija koja dohvaća letove određenog aerodroma
     * @param korisnik
     * @param lozinka
     * @param icao
     * @param odVremena
     * @param doVremena
     * @return 
     */
    public List<AvionLeti> dajLetoveAvionaAerodrom(String korisnik, String lozinka, String icao, long odVremena, long doVremena){
        List<AvionLeti> avioni = new ArrayList<>();
        try {
            org.foi.nwtis.skisic.ws.serveri.ProjektWS_Service service = new org.foi.nwtis.skisic.ws.serveri.ProjektWS_Service();
            org.foi.nwtis.skisic.ws.serveri.ProjektWS port = service.getProjektWSPort();
            avioni = port.dajLetoveAvionaAerodrom(korisnik, lozinka, icao, odVremena, doVremena);
        } catch (Exception ex) {
            ex.getMessage();
        }
        return avioni;
    }
    
    /**
     * Funkcija koja dohvaća letove određenog aviona u nekom razdoblju
     * @param korisnik
     * @param lozinka
     * @param icao24
     * @param odVremena
     * @param doVremena
     * @return 
     */
    public List<AvionLeti> dajLetoveAvionaRazdoblje(String korisnik, String lozinka, String icao24, long odVremena, long doVremena){
        List<AvionLeti> avioni = new ArrayList<>();
        try {
            org.foi.nwtis.skisic.ws.serveri.ProjektWS_Service service = new org.foi.nwtis.skisic.ws.serveri.ProjektWS_Service();
            org.foi.nwtis.skisic.ws.serveri.ProjektWS port = service.getProjektWSPort();
            avioni = port.dajLetoveAviona(korisnik, lozinka, icao24, odVremena, doVremena);
        } catch (Exception ex) {
            ex.getMessage();
        }
        return avioni;
    }
    
    /**
     * Funkcija koja dohvaća aerodrome koji se nalaze unutar određenih granica
     * @param korisnik
     * @param lozinka
     * @param icao - icao kod aerodroma
     * @param granicaOd - min udaljenost
     * @param granicaDo - max udaljenost
     * @return 
     */
    public List<Aerodrom> dajAerodromeGranica(String korisnik, String lozinka, String icao, int granicaOd, int granicaDo){
        List<Aerodrom> aerodromi = new ArrayList<>();
        try {
            org.foi.nwtis.skisic.ws.serveri.ProjektWS_Service service = new org.foi.nwtis.skisic.ws.serveri.ProjektWS_Service();
            org.foi.nwtis.skisic.ws.serveri.ProjektWS port = service.getProjektWSPort();
            aerodromi = port.dajAerodromeGranica(korisnik, lozinka, icao, granicaOd, granicaDo);
        } catch (Exception ex) {
            ex.getMessage();
        }
        return aerodromi;
    }
    
    /**
     * Funkcija koja daje udaljenost između dva aerodroma
     * @param korisnik
     * @param lozinka
     * @param icao1 icao kod prvog aerodroma
     * @param icao2 icao kod drugog aerodroma
     * @return 
     */
    public double dajUdaljenostAerodroma(String korisnik, String lozinka, String icao1, String icao2){
        try {
            org.foi.nwtis.skisic.ws.serveri.ProjektWS_Service service = new org.foi.nwtis.skisic.ws.serveri.ProjektWS_Service();
            org.foi.nwtis.skisic.ws.serveri.ProjektWS port = service.getProjektWSPort();
            double udaljenost = port.dajUdaljenostAerodroma(korisnik, lozinka, icao1, icao2);
            return udaljenost;
        } catch (Exception ex) {
            ex.getMessage();
        }
        return 0;
    }
}
