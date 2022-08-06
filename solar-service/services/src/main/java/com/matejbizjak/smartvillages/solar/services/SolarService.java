package com.matejbizjak.smartvillages.solar.services;

//import com.kumuluz.ee.logs.LogManager;
//import com.kumuluz.ee.logs.Logger;
import com.matejbizjak.smartvillages.solar.lib.v1.Energy;
import com.matejbizjak.smartvillages.solar.persistence.Solar;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@ApplicationScoped
public class SolarService {

//    private Logger LOG = LogManager.getLogger(SolarService.class.getSimpleName());

    @PersistenceContext(unitName = "main-jpa-unit")
    private EntityManager em;

    public Solar getSolar(String solarId) {
        return em.find(Solar.class, solarId);
    }

    public void storeEnergy(Energy energy, String solarId) {

    }
}
