package org.foi.nwtis.skisic.rest.klijenti;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
/**
 * Klasa koja služi kao klijent za RS web servis
 * @author Sara
 */
public class ProjektRS_2 {

    private final WebTarget webTarget;
    private final Client client;
    private static final String BASE_URI = "http://localhost:8084/skisic_web_modul_2/webresources";
    private String korisnik = "";
    private String lozinka = "";

    public ProjektRS_2() {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("aerodromi");
    }
    
    public ProjektRS_2(String korisnik, String lozinka) {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("aerodromi");
        this.korisnik = korisnik;
        this.lozinka = lozinka;
    }
    
    /**
     * Funkcija za brisanje aviona određenog aerodroma
     * @param icao
     * @return
     * @throws ClientErrorException 
     */
    public Response obrisiAvioneAerodroma(String icao) throws ClientErrorException {
        return webTarget
                .path(java.text.MessageFormat.format("{0}/avioni", new Object[]{icao}))
                .request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header("korisnik", korisnik)
                .header("lozinka", lozinka)
                .delete();
    }

    /**
     * Funkcija ispis podataka o aerodromu kojeg prati korisnik
     * @param <T>
     * @param responseType
     * @param icao
     * @return
     * @throws ClientErrorException 
     */
    public <T> T dajAerodomKorisnika(Class<T> responseType, String icao) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{icao}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header("korisnik", korisnik)
                .header("lozinka", lozinka)
                .get(responseType);
    }

    /**
     * Funkcija koja dohvaća sve aerodrome ili filtrirane aerodrome na temelju naziva ili države
     * @param <T>
     * @param responseType
     * @param drzava
     * @param naziv
     * @return
     * @throws ClientErrorException 
     */
    public <T> T dajAerodome(Class<T> responseType, String drzava, String naziv) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (drzava != null) {
            resource = resource.queryParam("drzava", drzava);
        }
        if (naziv != null) {
            resource = resource.queryParam("naziv", naziv);
        }
        resource = resource.path("svi");
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header("korisnik", korisnik)
                .header("lozinka", lozinka)
                .get(responseType);
    }

    /**
     * Funkcija za brisanje aerodroma korisnika
     * @param icao
     * @return
     * @throws ClientErrorException 
     */
    public Response obrisiAerodomKorisnika(String icao) throws ClientErrorException {
        return webTarget
                .path(java.text.MessageFormat.format("{0}", new Object[]{icao}))
                .request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header("korisnik", korisnik)
                .header("lozinka", lozinka)
                .delete();
    }

    /**
     * Funkcija koja dohvaća aerodrome koje korisnik prati
     * @param <T>
     * @param responseType
     * @return
     * @throws ClientErrorException 
     */
    public <T> T dajAerodomeKorisnika(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = webTarget;
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header("korisnik", korisnik)
                .header("lozinka", lozinka)
                .get(responseType);
    }

    public void close() {
        client.close();
    }
    
}
