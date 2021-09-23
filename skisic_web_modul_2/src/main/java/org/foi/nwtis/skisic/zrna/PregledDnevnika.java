package org.foi.nwtis.skisic.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.skisic.eb.Dnevnikrada;
import org.foi.nwtis.skisic.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.skisic.sb.DnevnikradaFacadeLocal;

@Named(value = "pregledDnevnika")
@SessionScoped
public class PregledDnevnika implements Serializable {
    
    @EJB(beanName = "DnevnikradaFacade")
    DnevnikradaFacadeLocal dnevnikradaFacade;
    
    @Inject
    ServletContext context;    
    
    @Inject
    Prijava prijava;
    
    @Getter
    @Setter
    String korisnik;
    
    @Getter
    @Setter
    List<Dnevnikrada> prikaziDnevnik;
    
    @Getter
    @Setter
    int stranicenje;
    
    String adresa = "";

    public PregledDnevnika() {
        
    }
    /**
     * Funkcija za učitavanje korisničkog imena i podatka za straničenje u konfiguraciji
     */
    @PostConstruct
    public void ucitajKonfiguraciju(){
        korisnik = prijava.getKorisnik();
        BP_Konfiguracija bpk = (BP_Konfiguracija) context.getAttribute("BP_Konfig");
        stranicenje = Integer.parseInt(bpk.getKonfig().dajPostavku("stranicenje"));
    }
    
    /**
     * Funkcija koja prikazuje poopis svih unosa u bazi podataka u tablici dnevnik rada
     * @return 
     */
    public List<Dnevnikrada> prikaziDnevnik(){
        List<Dnevnikrada> dnevnik = dnevnikradaFacade.findAll();
        List<Dnevnikrada> dnevnikKorisnika = new ArrayList<>();
        for(Dnevnikrada dr : dnevnik){
            if(dr.getKorisnik().equals(korisnik)){
                dnevnikKorisnika.add(dr);
            }
        }
        return dnevnikKorisnika;
    }
}
