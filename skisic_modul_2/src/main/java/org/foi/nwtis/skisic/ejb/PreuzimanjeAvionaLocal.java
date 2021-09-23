/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.skisic.ejb;

import javax.ejb.Local;
import org.foi.nwtis.skisic.konfiguracije.bp.BP_Konfiguracija;

/**
 *
 * @author Sara
 */
@Local
public interface PreuzimanjeAvionaLocal {

    void preuzmiAvione(BP_Konfiguracija bpk);
    
}
