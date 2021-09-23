package org.foi.nwtis.skisic.podaci;


public class AerodromiKorisnika {
    
    
    public String korisnik;
    public String ident;
    public String drzava;
    public String naziv;
    public int brojKorisnika;
    public int brojDanaLetova;
    public int brojPreuzetihLetova;
    
    public AerodromiKorisnika(){
        
    }
    
    public AerodromiKorisnika(String korisnik, String ident, String naziv, String drzava, int brojKorisnika, int brojDanaLetova, 
            int brojPreuzetihLetova){
        this.korisnik = korisnik;
        this.ident = ident;
        this.naziv = naziv;
        this.drzava = drzava;
        this.brojKorisnika = brojKorisnika;
        this.brojDanaLetova = brojDanaLetova;
        this.brojPreuzetihLetova = brojPreuzetihLetova;
    }

    public String getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(String korisnik) {
        this.korisnik = korisnik;
    }

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public String getDrzava() {
        return drzava;
    }

    public void setDrzava(String drzava) {
        this.drzava = drzava;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public int getBrojKorisnika() {
        return brojKorisnika;
    }

    public void setBrojKorisnika(int brojKorisnika) {
        this.brojKorisnika = brojKorisnika;
    }

    public int getBrojDanaLetova() {
        return brojDanaLetova;
    }

    public void setBrojDanaLetova(int brojDanaLetova) {
        this.brojDanaLetova = brojDanaLetova;
    }

    public int getBrojPreuzetihLetova() {
        return brojPreuzetihLetova;
    }

    public void setBrojPreuzetihLetova(int brojPreuzetihLetova) {
        this.brojPreuzetihLetova = brojPreuzetihLetova;
    }
    
    
}
