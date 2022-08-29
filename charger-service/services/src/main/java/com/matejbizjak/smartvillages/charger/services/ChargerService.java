package com.matejbizjak.smartvillages.charger.services;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.matejbizjak.smartvillages.charger.persistence.ChargerEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
@Transactional
public class ChargerService {

    private final Logger LOG = LogManager.getLogger(ChargerService.class.getSimpleName());

    @PersistenceContext(unitName = "main-jpa-unit")
    private EntityManager em;

    public List<ChargerEntity> getAll() {
        TypedQuery<ChargerEntity> query = em.createNamedQuery(ChargerEntity.FIND_ALL, ChargerEntity.class);
        return query.getResultList();
    }

    public ChargerEntity getCharger(String chargerId) {
        return em.find(ChargerEntity.class, chargerId);
    }

}
