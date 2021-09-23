/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.skisic.sb;

import java.util.List;
import javax.ejb.Local;
import org.foi.nwtis.skisic.eb.Dnevnikrada;

/**
 *
 * @author Sara
 */
@Local
public interface DnevnikradaFacadeLocal {

    void create(Dnevnikrada dnevnikrada);

    void edit(Dnevnikrada dnevnikrada);

    void remove(Dnevnikrada dnevnikrada);

    Dnevnikrada find(Object id);

    List<Dnevnikrada> findAll();

    List<Dnevnikrada> findRange(int[] range);

    int count();
    
}
