package com.matejbizjak.smartvillages.solar.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.logs.enums.LogLevel;
import com.kumuluz.ee.nats.common.util.SerDes;
import com.kumuluz.ee.nats.jetstream.annotations.JetStreamProducer;
import com.kumuluz.ee.nats.jetstream.util.JetStreamMessage;
import com.matejbizjak.smartvillages.solar.lib.v1.Energy;
import com.matejbizjak.smartvillages.solar.lib.v1.EnergySolarIntervalForSolar;
import com.matejbizjak.smartvillages.solar.lib.v1.EnergySolarIntervalForUser;
import com.matejbizjak.smartvillages.solar.persistence.EnergyEntity;
import com.matejbizjak.smartvillages.solar.persistence.SolarEntity;
import com.matejbizjak.smartvillages.userlib.v1.User;
import io.nats.client.JetStream;
import io.nats.client.PullSubscribeOptions;
import io.nats.client.PushSubscribeOptions;
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
    private SolarService solarService;

    @Inject
    @JetStreamProducer(connection = "secure")
    private JetStream jetStream;

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

    public EnergySolarIntervalForSolar getSolarEnergyDuringTimePeriod(String solarId, Instant startTime, Instant endTime) {
        Query query = em.createNamedQuery(EnergyEntity.FIND_SOLAR_ENERGY_DURING_TIME_PERIOD, EnergyEntity.class);
        query.setParameter("solarId", solarId);
        query.setParameter("startTime", startTime);
        query.setParameter("endTime", endTime);

        EnergySolarIntervalForSolar response = new EnergySolarIntervalForSolar();
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

    public EnergySolarIntervalForUser getSolarEnergyDuringTimePeriodForUser(User user, Instant startTime, Instant endTime) {
        EnergySolarIntervalForUser data = new EnergySolarIntervalForUser();
        data.setUser(user);
        List<EnergySolarIntervalForSolar> solarEnergyForIntervalList = new ArrayList<>();
        data.setEnergySolarList(solarEnergyForIntervalList);
        final BigDecimal[] energySum = {new BigDecimal(0)};

        List<SolarEntity> usersSolars = solarService.getSolarForUser(user.getId());
        usersSolars.forEach(solar -> {
            EnergySolarIntervalForSolar solarEnergyForInterval = getSolarEnergyDuringTimePeriod(solar.getId(), startTime, endTime);
            solarEnergyForIntervalList.add(solarEnergyForInterval);
            energySum[0] = energySum[0].add(solarEnergyForInterval.getSum());
        });
        data.setSum(energySum[0]);

        return data;
    }

    public List<EnergySolarIntervalForSolar> getAllSolarEnergyDuringTimePeriod(Instant startTime, Instant endTime, JetStreamMessage msg) {
        List<EnergySolarIntervalForSolar> energySolarList = new ArrayList<>();
        solarService.getAll().forEach(solar -> {
            energySolarList.add(getSolarEnergyDuringTimePeriod(solar.getId(), startTime, endTime));
            msg.inProgress();
        });
        return energySolarList;
    }

    public void sendDailyUserReportResponse(List<EnergySolarIntervalForSolar> energySolarList) {
        energySolarList.forEach(energySolar -> {
            NatsMessage message = null;
            try {
                message = NatsMessage.builder()
                        .subject("solar.energy.dailyReportRes")
                        .data(SerDes.serialize(energySolar))
                        .build();
            } catch (JsonProcessingException e) {
                LOG.log(LogLevel.ERROR, "Serialization error.", e);
                throw new RuntimeException(e);
            }
            jetStream.publishAsync(message);
            LOG.info("Sent data to subject " + "solar.energy.dailyReportRes");
        });
    }
}
