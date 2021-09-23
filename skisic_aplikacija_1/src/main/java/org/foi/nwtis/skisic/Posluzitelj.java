package org.foi.nwtis.skisic;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.skisic.dretve.ZahtjevPosluzitelja;
import org.foi.nwtis.skisic.konfiguracije.bp.BP_Konfiguracija;

public class Posluzitelj extends Thread{

    public int port = 0;

    BP_Konfiguracija bpk = null;
    ServerSocket ss = null;
    public Posluzitelj(BP_Konfiguracija bpk) {
        this.bpk = bpk;
    }
    
    /**
     * Provjerava zauzetost porta
     *
     * @param port - port servera simulatora leta
     * @return true ako je zauzet, false ako nije
     */
    public boolean zauzetiPort(int port) {
        boolean zauzeto = false;
        try (ServerSocket ss = new ServerSocket(port)) {
            zauzeto = false;
        } catch (Exception e) {
            zauzeto = true;
        }
        return zauzeto;
    }

    /**
     * Funkcija koja učitava parametre konfiguracije
     * @param bpk
     */
    public void ucitajKonfiguraciju(BP_Konfiguracija bpk) {
        port = Integer.parseInt(bpk.getKonfig().dajPostavku("port"));
    }

    /**
     * Funkcija koja prekida izvedbu dretve
     */
    @Override
    public void interrupt() {
        super.interrupt(); 
        if(ss != null){
            try {
                ss.close();
            } catch (IOException ex) {
                Logger.getLogger(Posluzitelj.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Funkcija za pokretanje dretve
     */
    @Override
    public void run() {
        ucitajKonfiguraciju(bpk);
        System.out.println("Port posluzitelj: " + port);
        if (!zauzetiPort(port)) {
            try {
                ss = new ServerSocket(port);
                while (true) {
                    Thread thread = new ZahtjevPosluzitelja(ss.accept(), bpk);
                    thread.start();
                }
            } catch (IOException ex) {
                Logger.getLogger(Posluzitelj.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("Port je zauzet!");
        }
    }

    /**
     * Funkcija za početnu inicijalizaciju dretve
     */
    @Override
    public synchronized void start() {
        super.start(); 
    }
    
    
    
}
