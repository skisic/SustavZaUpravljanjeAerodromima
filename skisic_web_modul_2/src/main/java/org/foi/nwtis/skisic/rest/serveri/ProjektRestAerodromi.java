package org.foi.nwtis.skisic.rest.serveri;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.foi.nwtis.podaci.Odgovor;
import org.foi.nwtis.rest.podaci.Lokacija;
import org.foi.nwtis.skisic.podaci.UpravljanjeAerodromima;
import org.foi.nwtis.skisic.ws.klijenti.ProjektWS_1;
import org.foi.nwtis.skisic.ws.serveri.Aerodrom;

/**
 * REST Web Service
 *
 * @author Sara
 */
@Path("aerodromi")
@RequestScoped
public class ProjektRestAerodromi {

    @Context
    private UriInfo context;
    
    @Inject
    UpravljanjeAerodromima upravljanjeAerodromima;
    
    public ProjektRestAerodromi() {
    }

    /**
     * Funkcija za dohvaćanje svih aerodroma koje korisnik prati
     * @param korisnik
     * @param lozinka
     * @return 
     */
    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajAerodomeKorisnika(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka) {
        ProjektWS_1 ws = new ProjektWS_1();
        List<Aerodrom> aerodromi = ws.dajAerodromeKorisnika(korisnik, lozinka);
        Odgovor odgovor = new Odgovor();
        odgovor.setStatus("10");
        odgovor.setPoruka("OK");
        String zahtjev = "Dohvaca sve aerodrome koji prati";
        upravljanjeAerodromima.upisiUDnevnik(korisnik, zahtjev);
        if (aerodromi == null) {
            aerodromi = new ArrayList<>();
            odgovor.setStatus("40");
            odgovor.setPoruka("Greška!");
        }
        odgovor.setOdgovor(aerodromi.toArray());
        return Response
                .ok(odgovor)
                .status(200)
                .build();
    }

    /**
     * Funkcija za dohvaćanje informacija o jednom aerodromu kojeg korisnik prati
     * @param korisnik
     * @param lozinka
     * @param icao
     * @return 
     */
    @Path("{icao}")
    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajAerodomKorisnika(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao) {
        ProjektWS_1 ws = new ProjektWS_1();
        List<Aerodrom> aerodrom = ws.dajAerodromKorisnika(korisnik, lozinka, icao);
        Odgovor odgovor = new Odgovor();
        odgovor.setStatus("10");
        odgovor.setPoruka("OK");
        String zahtjev = "Dohvaca podatke o aerodromu " + icao;
        upravljanjeAerodromima.upisiUDnevnik(korisnik, zahtjev);
        if (aerodrom == null) {
            aerodrom = new ArrayList<>();
            odgovor.setStatus("40");
            odgovor.setPoruka("Aerodrom ne postoji!");
        }
        odgovor.setOdgovor(aerodrom.toArray());
        return Response
                .ok(odgovor)
                .status(200)
                .build();
    }

    /**
     * Funkcija za dohvaćanje aerodroma koji sadrže određenu riječ u državi ili nazivu
     * Ako su ti parametri prazni, onda vraća sve aerodrome
     * @param korisnik
     * @param lozinka
     * @param naziv
     * @param drzava
     * @return 
     */
    @Path("/svi")
    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajAerodome(@HeaderParam("korisnik") String korisnik, @HeaderParam("lozinka") String lozinka,
            @QueryParam("naziv") String naziv, @QueryParam("drzava") String drzava) {
        ProjektWS_1 ws = new ProjektWS_1();
        List<Aerodrom> aerodromi = new ArrayList<>();
        Odgovor odgovor = new Odgovor();
        odgovor.setStatus("10");
        odgovor.setPoruka("OK");
        String zahtjev = "";
        if (naziv != null && !naziv.isEmpty()) {
            zahtjev = "Dohvaca aerodrome koji sadrze u nazivu " + naziv;
            upravljanjeAerodromima.upisiUDnevnik(korisnik, zahtjev);
            aerodromi = ws.dajAerodromeNaziv(korisnik, lozinka, naziv);
        } else if (drzava != null && !drzava.isEmpty()) {
            zahtjev = "Dohvaca aerodrome koji se nalaze u drzavi " + drzava;
            upravljanjeAerodromima.upisiUDnevnik(korisnik, zahtjev);
            aerodromi = ws.dajAerodromeDrzava(korisnik, lozinka, drzava);
        } else {
            zahtjev = "Dohvaca sve aerodrome";
            upravljanjeAerodromima.upisiUDnevnik(korisnik, zahtjev);
            aerodromi = ws.dajAerodromeNaziv(korisnik, lozinka, "");
        }
        if (aerodromi == null) {
            aerodromi = new ArrayList<>();
            odgovor.setStatus("40");
            odgovor.setPoruka("Aerodrom ne postoji!");
        }
        odgovor.setOdgovor(aerodromi.toArray());
        return Response
                .ok(odgovor)
                .status(200)
                .build();
    }
    
    /**
     * Funkcija koja briše aerodrom korisnika
     * @param korisnik
     * @param lozinka
     * @param icao
     * @return 
     */
    @Path("{icao}")
    @DELETE
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response obrisiAerodomKorisnika(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao) {
        ProjektWS_1 ws = new ProjektWS_1();
        boolean obrisano = upravljanjeAerodromima.obrisiAerodrom(korisnik, lozinka, icao);
        Odgovor odgovor = new Odgovor();
        odgovor.setStatus("10");
        odgovor.setPoruka("OK");
        String zahtjev = "Brise aerodrom " + icao;
        upravljanjeAerodromima.upisiUDnevnik(korisnik, zahtjev);
        if (obrisano == false) {
            odgovor.setStatus("40");
            odgovor.setPoruka("Greška prilikom brisanja!");
        }
        return Response
                .ok(odgovor)
                .status(200)
                .build();
    }
    
    /**
     * Funkcija koja briše sve letove aerodroma korisnika
     * @param korisnik
     * @param lozinka
     * @param icao
     * @return 
     */
    @Path("{icao}/avioni")
    @DELETE
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response obrisiAvioneAerodroma(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao) {
        boolean obrisano = upravljanjeAerodromima.obrisiAvioneAerodroma(korisnik, lozinka, icao);
        Odgovor odgovor = new Odgovor();
        odgovor.setStatus("10");
        odgovor.setPoruka("OK");
        String zahtjev = "Brise sve avione koji polijecu s aerodroma " + icao;
        upravljanjeAerodromima.upisiUDnevnik(korisnik, zahtjev);
        if (obrisano == false) {
            odgovor.setStatus("40");
            odgovor.setPoruka("Greška prilikom brisanja!");
        }
        return Response
                .ok(odgovor)
                .status(200)
                .build();
    }
}
