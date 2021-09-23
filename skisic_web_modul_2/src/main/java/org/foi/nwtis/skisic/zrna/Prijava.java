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

@Named(value = "prijava")
@SessionScoped
public class Prijava implements Serializable {

    @Inject
    ServletContext context;
    
    public Prijava() {
    }
    
    String tekst = "";
    int port;
    boolean status;
    
    @Getter
    @Setter
    String korisnik;
    
    @Getter
    @Setter
    String lozinka;
    
    @Getter
    @Setter
    boolean prijavljen;
    
    String adresa = "";
    
    /**
     * Funkcija za učitavanje konfiguracijskih podataka
     */
    @PostConstruct
    public void ucitajKonfiguraciju(){
        BP_Konfiguracija bpk = (BP_Konfiguracija) context.getAttribute("BP_Konfig");
        port = Integer.parseInt(bpk.getKonfig().dajPostavku("port"));
        status = Boolean.parseBoolean(bpk.getKonfig().dajPostavku("preuzimanje.status"));
        adresa = bpk.getKonfig().dajPostavku("adresa");
        setPrijavljen(false);
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
     * Funkcija za prijavu korisnika, šalje korisničko ime i lozinku na mrežnu utičnicu
     */
    public void prijaviSe(){
        ucitajKonfiguraciju();
        System.out.println("Port klijent: " + port);
        try {
            Socket s = new Socket(adresa, port);  
            tekst = "KORISNIK " + korisnik + "; LOZINKA " + lozinka + ";";
            posaljiPoruku(s, tekst);
            primiPoruku(s);
            if(tekst.equals("OK 10;")){
                setPrijavljen(true);
            } else {
                setPrijavljen(false);
            }
        } catch (Exception e) {
            System.out.println("Server je ugašen!");
        }
    }
    
    /**
     * Funkcija za odjavu korisnika, postavlja korisničko ime i lozinku na prazan string
     * @return vraća se na stranicu prijave
     */
    public String odjaviSe(){
        korisnik = "";
        lozinka = "";
        prijavljen = false;
        return "prijava";
    }
    
}
