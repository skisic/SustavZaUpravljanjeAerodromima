package org.foi.nwtis.skisic.zrna;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.net.Socket;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.skisic.konfiguracije.bp.BP_Konfiguracija;

@Named(value = "pregledStatusa")
@SessionScoped
public class PregledStatusa implements Serializable {
    
    String tekst;
    String korisnik = "";
    String lozinka = "";
    int port;
    
    @Inject
    ServletContext context;    
    
    @Inject
    Prijava prijava;
    
    @Getter
    @Setter
    Boolean status;
    
    @Getter
    @Setter
    String statusGrupe;
    
    @Getter
    @Setter
    String aerodromi;
    
    String adresa = "";

    public PregledStatusa() {
    }
    
    /**
     * Funkcija za učitavanje početnih vrijednosti konfiguracije
     */
    @PostConstruct
    public void ucitajKonfiguraciju(){
        BP_Konfiguracija bpk = (BP_Konfiguracija) context.getAttribute("BP_Konfig");
        port = Integer.parseInt(bpk.getKonfig().dajPostavku("port"));
        korisnik = prijava.getKorisnik();
        lozinka = prijava.getLozinka();
        adresa = bpk.getKonfig().dajPostavku("adresa");
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
     * Funkcija za provjeru trenutnog stanja preuzimanja
     */
    public void provjeriTrenutnoStanje(){
        ucitajKonfiguraciju();
        try {
            Socket s = new Socket(adresa, port);  
            tekst = "KORISNIK " + korisnik + "; LOZINKA " + lozinka + "; STANJE;";
            posaljiPoruku(s, tekst);
            primiPoruku(s);
            String t = tekst.split(";")[0];
            if(t.split(" ")[1].equals("11")){
                status = true;
            } else {
                status = false;
            }
        } catch (Exception e) {
            System.out.println("Server je ugašen!");
        }
    }
    
    /**
     * Funkcija za pauziranje preuzimanja
     */
    public void pauzirajPreuzimanje(){
        ucitajKonfiguraciju();
        try {
            Socket s = new Socket(adresa, port);  
            tekst = "KORISNIK " + korisnik + "; LOZINKA " + lozinka + "; PAUZA;";
            posaljiPoruku(s, tekst);
            primiPoruku(s);
        } catch (Exception e) {
            System.out.println("Server je ugašen!");
        }
    }
    
    /**
     * Funkcija za nastavak preuzimanja
     */
    public void nastaviPreuzimanje(){
        ucitajKonfiguraciju();
        try {
            Socket s = new Socket(adresa, port);  
            tekst = "KORISNIK " + korisnik + "; LOZINKA " + lozinka + "; RADI;";
            posaljiPoruku(s, tekst);
            primiPoruku(s);
        } catch (Exception e) {
            System.out.println("Server je ugašen!");
        }
    }
    
    /**
     * Funkcija koja završava preuzimanje i rad s mrežnom utičnicom
     */
    public void zavrsiRad(){
        ucitajKonfiguraciju();
        try {
            Socket s = new Socket(adresa, port);  
            tekst = "KORISNIK " + korisnik + "; LOZINKA " + lozinka + "; KRAJ;";
            posaljiPoruku(s, tekst);
            primiPoruku(s);
        } catch (Exception e) {
            System.out.println("Server je ugašen!");
        }
    }
    
    /**
     * Funkcija za registraciju grupe aerodroma
     */
    public void registrirajGrupu(){
        ucitajKonfiguraciju();
        try {
            Socket s = new Socket(adresa, port);  
            tekst = "KORISNIK " + korisnik + "; LOZINKA " + lozinka + "; GRUPA PRIJAVI;";
            posaljiPoruku(s, tekst);
            primiPoruku(s);
        } catch (Exception e) {
            System.out.println("Server je ugašen!");
        }
    }
    
    /**
     * Funkcija za deregistraciju grupe aerodroma
     */
    public void deregistrirajGrupu(){
        ucitajKonfiguraciju();
        try {
            Socket s = new Socket(adresa, port);  
            tekst = "KORISNIK " + korisnik + "; LOZINKA " + lozinka + "; GRUPA ODJAVI;";
            posaljiPoruku(s, tekst);
            primiPoruku(s);
        } catch (Exception e) {
            System.out.println("Server je ugašen!");
        }
    }
    
    /**
     * Funkcija za aktiviranje grupe aerodroma
     */
    public void aktivirajGrupu(){
        ucitajKonfiguraciju();
        try {
            Socket s = new Socket(adresa, port);  
            tekst = "KORISNIK " + korisnik + "; LOZINKA " + lozinka + "; GRUPA AKTIVIRAJ;";
            posaljiPoruku(s, tekst);
            primiPoruku(s);
        } catch (Exception e) {
            System.out.println("Server je ugašen!");
        }
    }
    
    /**
     * Funkcija za blokiranje grupe aerodroma
     */
    public void blokirajGrupu(){
        ucitajKonfiguraciju();
        try {
            Socket s = new Socket(adresa, port);  
            tekst = "KORISNIK " + korisnik + "; LOZINKA " + lozinka + "; GRUPA BLOKIRAJ;";
            posaljiPoruku(s, tekst);
            primiPoruku(s);
        } catch (Exception e) {
            System.out.println("Server je ugašen!");
        }
    }
    
    /**
     * Funkcija koja daje trenutno stanje grupe aerodroma
     */
    public void dajStanjeGrupe(){
        ucitajKonfiguraciju();
        try {
            Socket s = new Socket(adresa, port);  
            tekst = "KORISNIK " + korisnik + "; LOZINKA " + lozinka + "; GRUPA STANJE;";
            posaljiPoruku(s, tekst);
            primiPoruku(s);
            switch (tekst) {
                case "OK 21;":
                    statusGrupe = "AKTIVAN";
                    break;
                case "OK 22;":
                    statusGrupe = "BLOKIRAN";
                    break;
                case "ERR 21;":
                    statusGrupe = "NEPOSTOJI";
                    break;
                default:
                    statusGrupe = tekst;
                    break;
            }
        } catch (Exception e) {
            System.out.println("Server je ugašen!");
        }
    }
    
    /**
     * Funkcija za dodavanje aerodroma grupi
     */
    public void dodajAerodromeGrupi(){
        ucitajKonfiguraciju();
        try {
            Socket s = new Socket(adresa, port);  
            tekst = "KORISNIK " + korisnik + "; LOZINKA " + lozinka + "; GRUPA AERODROMI " + aerodromi + ";";
            posaljiPoruku(s, tekst);
            primiPoruku(s);
        } catch (Exception e) {
            System.out.println("Server je ugašen!");
        }
    }
}
