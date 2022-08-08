package com.matejbizjak.smartvillages.solar.services;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.matejbizjak.smartvillages.solar.persistence.SolarEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
@Transactional
public class SolarService {

    private final Logger LOG = LogManager.getLogger(SolarService.class.getSimpleName());

    @PersistenceContext(unitName = "main-jpa-unit")
    private EntityManager em;

    public List<SolarEntity> getAll() {
        TypedQuery<SolarEntity> query = em.createNamedQuery(SolarEntity.FIND_ALL, SolarEntity.class);
        return query.getResultList();
    }

    public SolarEntity getSolar(String solarId) {
        return em.find(SolarEntity.class, solarId);
    }

}
