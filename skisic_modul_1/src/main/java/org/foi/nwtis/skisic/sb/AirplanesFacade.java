/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.skisic.sb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.foi.nwtis.skisic.eb.Airplanes;
import java.util.List;

/**
 *
 * @author Sara
 */
@Stateless
public class AirplanesFacade extends AbstractFacade<Airplanes> implements AirplanesFacadeLocal {

    @PersistenceContext(unitName = " NWTiS_DZ3_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AirplanesFacade() {
        super(Airplanes.class);
    }
    
    @Override
    public List<Airplanes> dajLetoveAvionaAerodrom(String icao) {
        javax.persistence.criteria.CriteriaBuilder cb = em.getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery<Airplanes> cq = cb.createQuery(Airplanes.class); 
        javax.persistence.criteria.Root<Airplanes> rt = cq.from(Airplanes.class); 
        javax.persistence.criteria.ParameterExpression<String> parametar = cb.parameter(String.class);
        cq.select(rt).where(cb.equal(rt.get("estdepartureairport"), parametar)); 
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        q.setParameter(parametar, icao);
        List<Airplanes> avioni = q.getResultList();
        return avioni;
    }  
    
    @Override
    public List<Airplanes> dajLetoveAviona(String icao24) {
        javax.persistence.criteria.CriteriaBuilder cb = em.getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery<Airplanes> cq = cb.createQuery(Airplanes.class); 
        javax.persistence.criteria.Root<Airplanes> rt = cq.from(Airplanes.class); 
        javax.persistence.criteria.ParameterExpression<String> parametar = cb.parameter(String.class);
        cq.select(rt).where(cb.equal(rt.get("icao24"), parametar)); 
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        q.setParameter(parametar, icao24);
        List<Airplanes> avioni = q.getResultList();
        return avioni;
    }
    
}
