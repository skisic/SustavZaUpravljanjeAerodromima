package org.foi.nwtis.skisic.zrna;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.skisic.ws.klijenti.ProjektWS_1;
import org.foi.nwtis.skisic.ws.serveri.Aerodrom;
import org.foi.nwtis.skisic.ws.serveri.AvionLeti;

@Named(value = "pregledLetova")
@ViewScoped
public class PregledLetova implements Serializable{
    
    @Inject
    Prijava prijava;
    
    @Getter
    @Setter
    List<Aerodrom> dajAerodromeKorisnika;
    
    @Getter
    @Setter
    List<AvionLeti> letovi = new ArrayList<>();
    
    @Getter
    @Setter
    List<AvionLeti> letovi2 = new ArrayList<>();

    @Getter
    @Setter
    Aerodrom odabraniAerodrom;
    
    @Getter
    @Setter
    AvionLeti odabraniAvion;
    
    @Getter
    String korisnik;
    
    @Getter
    String lozinka;
    
    @Getter
    @Setter
    String icao;
    
    @Getter
    @Setter
    String datumOd;
    
    @Getter
    @Setter
    String datumDo;

    public PregledLetova() {
    }
    
    /**
     * Funkcija koja dohvaća korisničke podatke o prijavi
     */
    @PostConstruct
    public void dohvatiKorisnickePodatke(){
        korisnik = prijava.getKorisnik();
        lozinka = prijava.getLozinka();
    }
    
    /**
     * Funkcija koja dohvaća aerodrome korisnika pozivajući web servis
     * @return 
     */
    public List<Aerodrom> dajAerodromeKorisnika(){
        ProjektWS_1 ws = new ProjektWS_1();
        return ws.dajAerodromeKorisnika(korisnik, lozinka);
    }
    
    /**
     * Funkcija koja pretvara datum u UNIX timestamp
     * @param datum
     * @return
     * @throws ParseException 
     */
    public long pretvoriDatumULong(String datum) throws ParseException{
        Date dat;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        dat = sdf.parse(datum);
        return dat.getTime() / 1000L;
    }
    
    /**
     * Funkcija koja pretvara UNIX timestamp u datum
     * @param vrijeme
     * @return 
     */
    public String pretvoriLongUDatum(long vrijeme){
        Date dat = new Date(vrijeme * 1000);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        String datum1 = df.format(dat);
        return datum1;
    }
    
    /**
     * Funkcija koja dohvaća letova aviona određenog aerodroma
     */
    public void dajLetoveAvionaAerodrom(){
        ProjektWS_1 ws = new ProjektWS_1();
        letovi = new ArrayList<>();
        try {
            long odVremena = pretvoriDatumULong(datumOd);
            long doVremena = pretvoriDatumULong(datumDo);
            System.out.println("Preuzimanje letova ...");
            letovi = ws.dajLetoveAvionaAerodrom(korisnik, lozinka, odabraniAerodrom.getIcao(), odVremena, doVremena);
        } catch (ParseException ex) {
            ex.getMessage();
        }
    }
    
    /**
     * Funkcija koja dohvaća letove aviona
     */
    public void dajLetoveAviona(){
        ProjektWS_1 ws = new ProjektWS_1();
         letovi2 = new ArrayList<>();
        try {
            long odVremena = pretvoriDatumULong(datumOd);
            long doVremena = pretvoriDatumULong(datumDo);
            System.out.println("Preuzimanje letova ...");
            letovi2 = ws.dajLetoveAvionaRazdoblje(korisnik, lozinka, odabraniAvion.getIcao24(), odVremena, doVremena);
        } catch (ParseException ex) {
            ex.getMessage();
        }
    }
}
