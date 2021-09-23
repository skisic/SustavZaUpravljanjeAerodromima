package org.foi.nwtis.skisic.ws.klijenti;

import org.foi.nwtis.dkermek.ws.serveri.Aerodrom;
import org.foi.nwtis.dkermek.ws.serveri.StatusKorisnika;


public class AerodromiWS_1 {
    
    public boolean registrirajGrupu(String korisnickoIme, String korisnickaLozinka){
        boolean registrirano = false;
        try {
            org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service service = new org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service();
            org.foi.nwtis.dkermek.ws.serveri.AerodromiWS port = service.getAerodromiWSPort();
            registrirano = port.registrirajGrupu(korisnickoIme, korisnickaLozinka);
        } catch (Exception ex) {
            ex.getMessage();
        }
        return registrirano;
    }
    
    public boolean deregistrirajGrupu(String korisnickoIme, String korisnickaLozinka){
        boolean deregistrirano = false;
        try {
            org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service service = new org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service();
            org.foi.nwtis.dkermek.ws.serveri.AerodromiWS port = service.getAerodromiWSPort();
            deregistrirano = port.deregistrirajGrupu(korisnickoIme, korisnickaLozinka);
        } catch (Exception ex) {
            ex.getMessage();
        }
        return deregistrirano;
    }
    
    public String provjeriStatusGrupe(String korisnickoIme, String korisnickaLozinka){
        StatusKorisnika statusGrupe = null;
        try {
            org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service service = new org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service();
            org.foi.nwtis.dkermek.ws.serveri.AerodromiWS port = service.getAerodromiWSPort();
            statusGrupe = port.dajStatusGrupe(korisnickoIme, korisnickaLozinka);
        } catch (Exception ex) {
            ex.getMessage();
        }
        return statusGrupe.toString();
    }
    
    public boolean aktivirajGrupu(String korisnickoIme, String korisnickaLozinka){
        boolean aktivirano = false;
        try {
            org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service service = new org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service();
            org.foi.nwtis.dkermek.ws.serveri.AerodromiWS port = service.getAerodromiWSPort();
            aktivirano = port.aktivirajGrupu(korisnickoIme, korisnickaLozinka);
        } catch (Exception ex) {
            ex.getMessage();
        }
        return aktivirano;
    }
    
    public boolean blokirajGrupu(String korisnickoIme, String korisnickaLozinka){
        boolean blokirano = false;
        try {
            org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service service = new org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service();
            org.foi.nwtis.dkermek.ws.serveri.AerodromiWS port = service.getAerodromiWSPort();
            blokirano = port.blokirajGrupu(korisnickoIme, korisnickaLozinka);
        } catch (Exception ex) {
            ex.getMessage();
        }
        return blokirano;
    }
    
    public boolean dodajAerodromGrupi(String korisnickoIme, String korisnickaLozinka, Aerodrom aerodrom){
        boolean dodaj = false;
        try {
            org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service service = new org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service();
            org.foi.nwtis.dkermek.ws.serveri.AerodromiWS port = service.getAerodromiWSPort();
            dodaj = port.dodajAerodromGrupi(korisnickoIme, korisnickaLozinka, aerodrom);
        } catch (Exception ex) {
            ex.getMessage();
        }
        return dodaj;
    }
    
    public boolean obrisiAerodromeGrupe(String korisnickoIme, String korisnickaLozinka){
        boolean obrisano = false;
        try {
            org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service service = new org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service();
            org.foi.nwtis.dkermek.ws.serveri.AerodromiWS port = service.getAerodromiWSPort();
            obrisano = port.obrisiSveAerodromeGrupe(korisnickoIme, korisnickaLozinka);
        } catch (Exception ex) {
            ex.getMessage();
        }
        return obrisano;
    }
    
    public boolean dodajAerodromIcaoGrupi(String korisnickoIme, String korisnickaLozinka, String icao){
        boolean dodano = false;
        try {
            org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service service = new org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service();
            org.foi.nwtis.dkermek.ws.serveri.AerodromiWS port = service.getAerodromiWSPort();
            dodano = port.dodajAerodromIcaoGrupi(korisnickoIme, korisnickaLozinka, icao);
        } catch (Exception ex) {
            ex.getMessage();
        }
        return dodano;
    }
    
    
    
}
