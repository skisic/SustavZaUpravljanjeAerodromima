package org.foi.nwtis.skisic.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.podaci.Korisnik;
import org.foi.nwtis.skisic.konfiguracije.bp.BP_Konfiguracija;

/**
 * Klasa za rad s bazom podataka i korisnicima
 */
public class KorisnikDAO {

    /**
     * Funkcija koja dohvaca sve korisnike
     * @param korisnik korisnicko ime
     * @param lozinka lozinka
     * @param prijava provjera je li korisnik prijavljen
     * @param bpk konfiguracija
     * @return 
     */
    public Korisnik dohvatiKorisnika(String korisnik, String lozinka, Boolean prijava, BP_Konfiguracija bpk) {
        String url = bpk.getServerDatabase() + bpk.getUserDatabase();
        String bpkorisnik = bpk.getUserUsername();
        String bplozinka = bpk.getUserPassword();
        String upit = "SELECT IME, PREZIME, EMAIL_ADRESA, KOR_IME, LOZINKA FROM korisnici WHERE "
                + "KOR_IME = '" + korisnik + "'";

        if (prijava) {
            upit += " and LOZINKA = '" + lozinka + "'";
        }

        try {
            Class.forName(bpk.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {

                while (rs.next()) {
                    String korisnik1 = rs.getString("kor_ime");
                    String ime = rs.getString("ime");
                    String prezime = rs.getString("prezime");
                    String email = rs.getString("email_adresa");
                    Timestamp kreiran = rs.getTimestamp("datum_kreiranja");
                    Timestamp promjena = rs.getTimestamp("datum_promjene");
                    Korisnik k = new Korisnik(korisnik1, ime, prezime, "*******", email, kreiran, promjena);
                    return k;
                }

            } catch (SQLException ex) {
                Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Funkcija za azuriranje korisnika 
     * @param k objekt korisnika
     * @param bpk konfiguracija
     * @return true ili false
     */
    public boolean azurirajKorisnika(Korisnik k, BP_Konfiguracija bpk) {
        String url = bpk.getServerDatabase() + bpk.getUserDatabase();
        String bpkorisnik = bpk.getUserUsername();
        String bplozinka = bpk.getUserPassword();
        String upit = "UPDATE korisnici SET IME = '" + k.getIme() + "', PREZIME = '" + k.getPrezime()
                + "', EMAIL_ADRESA = '" + k.getEmailAdresa() + "', LOZINKA = '" + k.getLozinka() + "' WHERE "
                + "KOR_IME = '" + k.getKorIme() + "'";
        String upitBezLozinke = "UPDATE korisnici SET IME = '" + k.getIme() + "', PREZIME = '" + k.getPrezime()
                + "', EMAIL_ADRESA = '" + k.getEmailAdresa() + "' WHERE "
                + "KOR_IME = '" + k.getKorIme() + "'";

        try {
            Class.forName(bpk.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement()) {
                int brojAzuriranja;
                if (k.getIme() != null || !k.getIme().trim().isEmpty() || k.getPrezime() != null || k.getPrezime().trim().isEmpty()
                        || k.getEmailAdresa() != null || k.getEmailAdresa().trim().isEmpty()
                        || k.getKorIme() != null || k.getKorIme().trim().isEmpty()) {
                    if (k.getLozinka().trim().isEmpty() || k.getLozinka() == null) {
                        brojAzuriranja = s.executeUpdate(upitBezLozinke);
                    } else {
                        brojAzuriranja = s.executeUpdate(upit);
                    }
                    return brojAzuriranja == 1;
                }

            } catch (SQLException ex) {
                Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Funkcija za dodavanje korisnika u bazu podataka
     * @param k objekt korisnika
     * @param bpk konfiguracija
     * @return true ili false
     */
    public boolean dodajKorisnika(String korisnik, String lozinka, BP_Konfiguracija bpk) {
        String url = bpk.getServerDatabase() + bpk.getUserDatabase();
        String bpkorisnik = bpk.getUserUsername();
        String bplozinka = bpk.getUserPassword();
        String upit = "INSERT INTO korisnici (KORISNICKO_IME, LOZINKA) VALUES ("
                + "'" + korisnik + "', '" + lozinka
                + "')";

        try {
            Class.forName(bpk.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement()) {

                int brojAzuriranja = s.executeUpdate(upit);

                return brojAzuriranja == 1;

            } catch (SQLException ex) {
                Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Funkcija koja provjerava je li korisnik upisao tocno korisnicko ime i lozinku
     * @param korisnik korisnicko ime
     * @param lozinka lozinka
     * @param bpk konfiguracija
     * @return true ili false
     */
    public boolean provjeraKorisnika(String korisnik, String lozinka, BP_Konfiguracija bpk) {
        String url = bpk.getServerDatabase() + bpk.getUserDatabase();
        String bpkorisnik = bpk.getUserUsername();
        String bplozinka = bpk.getUserPassword();
        String upit = "SELECT KORISNICKO_IME, LOZINKA FROM korisnici WHERE "
                + "KORISNICKO_IME = '" + korisnik + "' AND LOZINKA = '" + lozinka + "'";
        try {
            Class.forName(bpk.getDriverDatabase(url));
            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    ResultSet rs = s.executeQuery(upit)) {
                return rs.first();
            } catch (SQLException ex) {
                Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Funkcija koja dohvaca sve korisnike
     * @param korisnik korisnicko ime
     * @param lozinka lozinka
     * @param bpk konfiguracija
     * @return lista korisnika ili null
     */
    public List<Korisnik> sviKorisnici(String korisnik, String lozinka, BP_Konfiguracija bpk) {
        String url = bpk.getServerDatabase() + bpk.getUserDatabase();
        String bpkorisnik = bpk.getUserUsername();
        String bplozinka = bpk.getUserPassword();
        String upit = "SELECT * FROM korisnici";
        try {
            Class.forName(bpk.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {

                List<Korisnik> korisnici = new ArrayList<>();

                while (rs.next()) {
                    String korisnik1 = rs.getString("kor_ime");
                    String ime = rs.getString("ime");
                    String prezime = rs.getString("prezime");
                    String email = rs.getString("email_adresa");
                    Timestamp kreiran = rs.getTimestamp("datum_kreiranja");
                    Timestamp promjena = rs.getTimestamp("datum_promjene");
                    Korisnik k = new Korisnik(korisnik1, ime, prezime, "*******", email, kreiran, promjena);
                    korisnici.add(k);
                }
                return korisnici;

            } catch (SQLException ex) {
                Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * Funkcija koja dohvaca sve korisnike koji su dodali barem jedan aerodrom
     * @param korisnik korisnicko ime
     * @param lozinka lozinka
     * @param bpk konfiguracija
     * @return lista korisnika ili null
     */
    public List<Korisnik> korisniciAerodroma(String korisnik, String lozinka, BP_Konfiguracija bpk) {
        String url = bpk.getServerDatabase() + bpk.getUserDatabase();
        String bpkorisnik = bpk.getUserUsername();
        String bplozinka = bpk.getUserPassword();
        String upit = "SELECT * FROM myairports, korisnici WHERE username = kor_ime";
        try {
            Class.forName(bpk.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {

                List<Korisnik> korisnici = new ArrayList<>();

                while (rs.next()) {
                    String korisnik1 = rs.getString("kor_ime");
                    String ime = rs.getString("ime");
                    String prezime = rs.getString("prezime");
                    String email = rs.getString("email_adresa");
                    Timestamp kreiran = rs.getTimestamp("datum_kreiranja");
                    Timestamp promjena = rs.getTimestamp("datum_promjene");
                    Korisnik k = new Korisnik(korisnik1, ime, prezime, "*******", email, kreiran, promjena);
                    korisnici.add(k);
                }
                return korisnici;

            } catch (SQLException ex) {
                Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public List<String> dohvatiImena(BP_Konfiguracija bpk) {
        String url = bpk.getServerDatabase() + bpk.getUserDatabase();
        String bpkorisnik = bpk.getUserUsername();
        String bplozinka = bpk.getUserPassword();
        String upit = "SELECT KORISNICKO_IME FROM korisnici";
        try {
            Class.forName(bpk.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {

                List<String> listaKorisnika = new ArrayList<>();

                while (rs.next()) {
                    String korisnik1 = rs.getString("korisnicko_ime");
                    listaKorisnika.add(korisnik1);
                }
                return listaKorisnika;

            } catch (SQLException ex) {
                Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
