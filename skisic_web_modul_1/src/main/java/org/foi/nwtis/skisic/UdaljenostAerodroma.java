package org.foi.nwtis.skisic;
/**
 * Klasa za izračunavanje udaljenosti između dvije koordinate
 * @author Sara
 */
public class UdaljenostAerodroma {
    
    /**
     * Funkcija za izračunavanje udaljenosti između dvije koordinate
     * @param lat1 - širina prve koordinate
     * @param lon1 - dućina prve koordinate
     * @param lat2 - širina druge koordinate
     * @param lon2 - dužina druge koordinate
     * @param mjernaJedinica - mjerna jedinica u kojoj se iskazuje udaljenost (K za km)
     * @return 
     */
    public double izracunajUdaljenost(double lat1, double lon1, double lat2, double lon2, char mjernaJedinica) {
      double theta = lon1 - lon2;
      double udaljenost = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * 
              Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
      udaljenost = Math.acos(udaljenost);
      udaljenost = rad2deg(udaljenost);
      udaljenost = udaljenost * 60 * 1.1515;
      if (mjernaJedinica == 'K') {
        udaljenost = udaljenost * 1.609344;
      } else if (mjernaJedinica == 'N') {
        udaljenost = udaljenost * 0.8684;
        }
      return (udaljenost);
    }

    /**
     * Funkcija koja pretvara stupnjeve u radijane
     * @param deg stupnjevi
     * @return 
     */
    private double deg2rad(double deg) {
      return (deg * Math.PI / 180.0);
    }

    /**
     * Funkcija koja pretvara radijane u stupnjeve
     * @param rad - radijani
     * @return 
     */
    private double rad2deg(double rad) {
      return (rad * 180.0 / Math.PI);
    }
}
