/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.skisic.sb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.foi.nwtis.skisic.eb.Airports;

/**
 *
 * @author Sara
 */
@Stateless
public class AirportsFacade extends AbstractFacade<Airports> implements AirportsFacadeLocal {

    @PersistenceContext(unitName = " NWTiS_DZ3_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AirportsFacade() {
        super(Airports.class);
    }
    
}
