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
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.skisic.eb.Korisnici;
import org.foi.nwtis.skisic.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.skisic.sb.KorisniciFacadeLocal;

@Named(value = "registracija")
@SessionScoped
public class Registracija implements Serializable {

    @Inject
    ServletContext context;
    
    @EJB(beanName = "KorisniciFacade")
    KorisniciFacadeLocal korisniciFacade;
    
    public Registracija() {
    }
    
    String tekst = "";
    int port;
    boolean status;
    List<Korisnici> listaKorisnika;
    Korisnici k;
    
    @Getter
    @Setter
    String korisnik;
    
    @Getter
    @Setter
    String lozinka;
    
    @Getter
    @Setter
    String ime;
    
    @Getter
    @Setter
    String prezime;
    
    @Getter
    @Setter
    String email;
    
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
        listaKorisnika = korisniciFacade.findAll();
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
     * Funkcija za registraciju, šalje poruku na poslužitelj
     */
    public void registrirajSe(){
        try {
            Socket s = new Socket(adresa, port);  
            tekst = "KORISNIK " + korisnik + "; LOZINKA " + lozinka + "; DODAJ;";
            dodajKorisnika();
            posaljiPoruku(s, tekst);
            primiPoruku(s);
        } catch (Exception e) {
            System.out.println("Server je ugašen!");
        }
    }
    
    /**
     * Funkcija za validaciju podataka prilikom registracije, provjerava je li bilo kojih od parametara prazan
     * @param korisnik korisničko ime
     * @param lozinka lozinka
     * @param ime ime korisnika
     * @param prezime prezime korisnika
     * @param email email adresa korisnika
     * @return 
     */
    public boolean validacijaPodataka(String korisnik, String lozinka, String ime, String prezime, String email){
        for(Korisnici k : listaKorisnika){
            if(k.getKorIme().equals(korisnik))
                return false;
        }
        if(lozinka.isEmpty() || lozinka.length() < 6)
            return false;
        if(ime.isEmpty() || prezime.isEmpty() || email.isEmpty())
            return false;
        return true;
    }
    
    /**
     * Funkcija za dodavanje korisnika u bazu podataka
     */
    public void dodajKorisnika(){
        Date danas = new Date();
        java.sql.Date trenutnoVrijeme = new java.sql.Date(danas.getTime());
        if(validacijaPodataka(korisnik, lozinka, ime, prezime, email)){
                k = new Korisnici();
                k.setKorIme(korisnik);
                k.setLozinka(lozinka);
                k.setIme(ime);
                k.setPrezime(prezime);
                k.setEmailAdresa(email);
                k.setDatumKreiranja(trenutnoVrijeme);
                k.setDatumPromjene(trenutnoVrijeme);
                korisniciFacade.create(k);
            }
    }
    
}
