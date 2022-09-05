package com.matejbizjak.smartvillages.house.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.logs.enums.LogLevel;
import com.kumuluz.ee.nats.common.util.SerDes;
import com.kumuluz.ee.nats.jetstream.annotations.JetStreamProducer;
import com.kumuluz.ee.nats.jetstream.wrappers.JetStreamMessage;
import com.matejbizjak.smartvillages.house.lib.v1.EnergyHouseIntervalForHouse;
import com.matejbizjak.smartvillages.house.lib.v1.HouseEnergy;
import com.matejbizjak.smartvillages.house.persistence.HouseEnergyEntity;
import io.nats.client.JetStream;
import io.nats.client.impl.NatsMessage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Transactional
public class EnergyService {

    @PersistenceContext(unitName = "main-jpa-unit")
    private EntityManager em;

    @Inject
    private HouseService houseService;

    @Inject
    @JetStreamProducer(connection = "main-secure")
    private JetStream jetStream;

    private final Logger LOG = LogManager.getLogger(EnergyService.class.getSimpleName());

    public void storeEnergy(HouseEnergy energy, String houseId) {
        HouseEnergyEntity entity = new HouseEnergyEntity();
        entity.setHouse(houseService.getHouse(houseId));
        entity.setValue(energy.getValue());
        entity.setStartTime(energy.getStartTime());
        entity.setDuration(energy.getDuration());

        em.persist(entity);
        LOG.info(String.format("Stored %s W of energy from house %s to the database.", energy.getValue(), houseId));
    }

    public EnergyHouseIntervalForHouse getHouseEnergyDuringTimePeriod(String houseId, Instant startTime, Instant endTime) {
        Query query = em.createNamedQuery(HouseEnergyEntity.FIND_HOUSE_ENERGY_DURING_TIME_PERIOD, HouseEnergyEntity.class);
        query.setParameter("houseId", houseId);
        query.setParameter("startTime", startTime);
        query.setParameter("endTime", endTime);

        EnergyHouseIntervalForHouse response = new EnergyHouseIntervalForHouse();
        response.setEnergyList(query.getResultList());
        response.setHouse(houseService.getHouse(houseId));
        response.setStartTime(startTime);
        response.setEndTime(endTime);
        response.setSum(
                response.getEnergyList()
                        .stream()
                        .map(HouseEnergyEntity::getValue)
                        .reduce(new BigDecimal(0), BigDecimal::add)
        );
        return response;
    }

    public List<EnergyHouseIntervalForHouse> getAllHouseEnergyDuringTimePeriod(Instant startTime, Instant endTime, JetStreamMessage msg) {
        List<EnergyHouseIntervalForHouse> energyHouseList = new ArrayList<>();
        houseService.getAll().forEach(house -> {
            energyHouseList.add(getHouseEnergyDuringTimePeriod(house.getId(), startTime, endTime));
            msg.inProgress();
        });
        return energyHouseList;
    }

    public void sendDailyUserReportResponse(List<EnergyHouseIntervalForHouse> energyHouseList) {
        energyHouseList.forEach(energyHouse -> {
            NatsMessage message = null;
            try {
                message = NatsMessage.builder()
                        .subject("house.energy.dailyReportRes")
                        .data(SerDes.serialize(energyHouse))
                        .build();
            } catch (JsonProcessingException e) {
                LOG.log(LogLevel.ERROR, "Serialization error.", e);
                throw new RuntimeException(e);
            }
            jetStream.publishAsync(message);
            LOG.info("Sent data to subject " + "house.energy.dailyReportRes");
        });
    }
}
