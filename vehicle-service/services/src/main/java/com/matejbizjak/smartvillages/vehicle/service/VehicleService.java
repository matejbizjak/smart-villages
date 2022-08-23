package com.matejbizjak.smartvillages.vehicle.service;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.matejbizjak.smartvillages.vehicle.persistence.VehicleEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
@Transactional
public class VehicleService {

    private final Logger LOG = LogManager.getLogger(VehicleService.class);

    @PersistenceContext(unitName = "main-jpa-unit")
    private EntityManager em;

    public List<VehicleEntity> getAll() {
        TypedQuery<VehicleEntity> query = em.createNamedQuery(VehicleEntity.FIND_ALL, VehicleEntity.class);
        return query.getResultList();
    }

    public VehicleEntity get(String vehicleId) {
        return em.find(VehicleEntity.class, vehicleId);
    }

    public List<VehicleEntity> getByUser(String userId) {
        TypedQuery<VehicleEntity> query = em.createNamedQuery(VehicleEntity.FIND_FOR_USER, VehicleEntity.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

}
