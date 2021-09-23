package org.foi.nwtis.skisic.slusaci;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.foi.nwtis.skisic.Posluzitelj;
import org.foi.nwtis.skisic.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.skisic.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.skisic.konfiguracije.bp.BP_Konfiguracija;

@WebListener
public class SlusacAplikacije implements ServletContextListener {
    
    Posluzitelj p = null;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        String datoteke = sc.getInitParameter("konfiguracija");
        String putanja = sc.getRealPath("WEB-INF") + File.separator + datoteke;
        try {
            BP_Konfiguracija konf = new BP_Konfiguracija(putanja);
            sc.setAttribute("BP_Konfig", konf);
            p = new Posluzitelj(konf);
            p.start();
        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("Aplikacija je pokrenuta!");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if(p != null){
            p.interrupt();
        }
        System.out.println("Aplikacija je zaustavljena!");
    }
}
