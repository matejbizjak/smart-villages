package com.matejbizjak.smartvillages.solar.services;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.matejbizjak.smartvillages.solar.lib.v1.Energy;
import com.matejbizjak.smartvillages.solar.persistence.EnergyEntity;
import com.matejbizjak.smartvillages.solar.services.responses.EnergyResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Instant;

@ApplicationScoped
@Transactional
public class EnergyService {

    @PersistenceContext(unitName = "main-jpa-unit")
    private EntityManager em;

    @Inject
    private SolarService solarService;

    private final Logger LOG = LogManager.getLogger(EnergyService.class.getSimpleName());

    public void storeEnergy(Energy energy, String solarId) {
        EnergyEntity entity = new EnergyEntity();
        entity.setSolarId(solarId);
        entity.setValue(energy.getValue());
        entity.setStartTime(energy.getStartTime());
        entity.setDuration(energy.getDuration());

        em.persist(entity);
        LOG.info(String.format("Stored %s W of energy from solar %s to the database.", energy.getValue(), solarId));
    }

    public EnergyResponse getSolarEnergyDuringTimePeriod(String solarId, Instant startTime, Instant endTime) {
        Query query = em.createNamedQuery(EnergyEntity.FIND_SOLAR_ENERGY_DURING_TIME_PERIOD, EnergyEntity.class);
        query.setParameter("solarId", solarId);
        query.setParameter("startTime", startTime);
        query.setParameter("endTime", endTime);

        EnergyResponse response = new EnergyResponse();
        response.setEnergyList(query.getResultList());
        response.setSolar(solarService.getSolar(solarId));
        response.setStartTime(startTime);
        response.setEndTime(endTime);
        response.setSum(
                response.getEnergyList()
                        .stream()
                        .map(EnergyEntity::getValue)
                        .reduce(new BigDecimal(0), BigDecimal::add)
        );
        return response;
    }
}
