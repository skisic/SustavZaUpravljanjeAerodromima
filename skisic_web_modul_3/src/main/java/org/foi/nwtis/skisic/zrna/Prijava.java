package org.foi.nwtis.skisic.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.skisic.eb.Korisnici;
import org.foi.nwtis.skisic.sb.AirplanesFacadeLocal;
import org.foi.nwtis.skisic.sb.AirportsFacadeLocal;
import org.foi.nwtis.skisic.sb.KorisniciFacadeLocal;
import org.foi.nwtis.skisic.sb.MyairportsFacadeLocal;
import org.foi.nwtis.skisic.sb.MyairportslogFacadeLocal;

@Named(value = "prijava")
@SessionScoped
public class Prijava implements Serializable {
    
    @EJB(beanName = "KorisniciFacade")
    KorisniciFacadeLocal korisniciFacade;

    @EJB(beanName = "MyairportsFacade")
    MyairportsFacadeLocal myairportsFacade;

    @EJB(beanName = "MyairportslogFacade")
    MyairportslogFacadeLocal myairportslogFacade;

    @EJB(beanName = "AirportsFacade")
    AirportsFacadeLocal airportsFacade;

    @EJB(beanName = "AirplanesFacade")
    AirplanesFacadeLocal airplanesFacade;
    
    @Inject
    ServletContext context;
    
    @Inject
    Lokalizacija lokalizacija;
    
    @Getter
    @Setter
    String korisnik;
    
    @Getter
    @Setter
    String lozinka;
    
    @Getter
    @Setter
    String poruka;
    
    @Getter
    boolean prijavljen;
    

    public Prijava() {
    }
    
    /**
     * Funkcija za autentikaciju korisnika
     * @param korisnik
     * @param lozinka
     * @return 
     */
    public boolean autentikacija(String korisnik, String lozinka){
        List<Korisnici> korisnici = korisniciFacade.findAll();
        for(Korisnici k : korisnici){
            if(k.getKorIme().equals(korisnik) && k.getLozinka().equals(lozinka)){
                return true;
            }
        }
        return false;
    }
    
    /**
     * Funkcija za prijavu korisnika
     */
    public void prijava(){
        ResourceBundle res = ResourceBundle.getBundle("org.foi.nwtis.skisic.web.Prijevod", new Locale(lokalizacija.getJezik()));
        
        if(autentikacija(korisnik, lozinka)){
            prijavljen = true;
            poruka = res.getString("prijava.uspjesnaPrijava");
        } else if (!autentikacija(korisnik, lozinka)){
            poruka = res.getString("prijava.pogresniPodaci");
        } else if (korisnik.isEmpty() || lozinka.isEmpty()){
            poruka = res.getString("prijava.prazniPodaci");
        } else if (prijavljen){
            poruka = res.getString("prijava.vecPrijavljen");
        }
        
    }
    
    /**
     * Funkcija za odjavu korisnika
     */
    public void odjava(){
        ResourceBundle res = ResourceBundle.getBundle("org.foi.nwtis.skisic.web.Prijevod", new Locale(lokalizacija.getJezik()));
        korisnik = "";
        lozinka = "";
        poruka = res.getString("prijava.uspjesnaOdjava");
        prijavljen = false;
    }
}
