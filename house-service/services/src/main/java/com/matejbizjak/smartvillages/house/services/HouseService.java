package com.matejbizjak.smartvillages.house.services;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.matejbizjak.smartvillages.house.persistence.HouseEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
@Transactional
public class HouseService {

    private final Logger LOG = LogManager.getLogger(HouseService.class.getSimpleName());

    @PersistenceContext(unitName = "main-jpa-unit")
    private EntityManager em;

    public List<HouseEntity> getAll() {
        TypedQuery<HouseEntity> query = em.createNamedQuery(HouseEntity.FIND_ALL, HouseEntity.class);
        return query.getResultList();
    }

    public HouseEntity getHouse(String houseId) {
        return em.find(HouseEntity.class, houseId);
    }

}
