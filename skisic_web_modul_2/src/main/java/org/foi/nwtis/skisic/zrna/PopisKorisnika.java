package org.foi.nwtis.skisic.zrna;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.foi.nwtis.skisic.eb.Korisnici;
import org.foi.nwtis.skisic.sb.KorisniciFacadeLocal;

@Named(value = "popisKorisnika")
@ViewScoped
public class PopisKorisnika implements Serializable{

    public PopisKorisnika() {
    }
    
    @EJB(beanName = "KorisniciFacade")
    KorisniciFacadeLocal korisniciFacade;
    
    /**
     * Funkcija koja dohvaća popis korisnika iz baze podataka
     * @return lista korisnika
     */
    public List<Korisnici> dajPopisKorisnika(){
        List<Korisnici> listaKorisnika = new ArrayList<>();
        listaKorisnika = korisniciFacade.findAll();
        return listaKorisnika;
    }
    
}
