package com.matejbizjak.smartvillages.charger.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.logs.enums.LogLevel;
import com.kumuluz.ee.nats.common.util.SerDes;
import com.kumuluz.ee.nats.jetstream.annotations.JetStreamProducer;
import com.kumuluz.ee.nats.jetstream.util.JetStreamMessage;
import com.matejbizjak.smartvillages.charger.lib.v1.ChargerEnergy;
import com.matejbizjak.smartvillages.charger.lib.v1.EnergyChargerIntervalForCharger;
import com.matejbizjak.smartvillages.charger.lib.v1.EnergyChargerIntervalForVehicle;
import com.matejbizjak.smartvillages.charger.persistence.ChargerEnergyEntity;
import com.matejbizjak.smartvillages.libutils.CommonUtil;
import com.matejbizjak.smartvillages.vehicle.lib.v1.Vehicle;
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
    private ChargerService chargerService;

    @Inject
    @JetStreamProducer(connection = "main-secure")
    private JetStream jetStream;

    private final Logger LOG = LogManager.getLogger(EnergyService.class.getSimpleName());

    public void storeEnergy(ChargerEnergy energy, String chargerId) {
        ChargerEnergyEntity entity = new ChargerEnergyEntity();
        entity.setCharger(chargerService.getCharger(chargerId));
        entity.setVehicleId(CommonUtil.safeNav(energy.getVehicle(), Vehicle::getId));
        entity.setValue(energy.getValue());
        entity.setStartTime(energy.getStartTime());
        entity.setDuration(energy.getDuration());

        em.persist(entity);
        LOG.info(String.format("Stored %s W of energy from charger %s to the database.", energy.getValue(), chargerId));
    }

    public EnergyChargerIntervalForCharger getChargerEnergyDuringTimePeriod(String chargerId, Instant startTime, Instant endTime) {
        Query query = em.createNamedQuery(ChargerEnergyEntity.FIND_CHARGER_ENERGY_DURING_TIME_PERIOD, ChargerEnergyEntity.class);
        query.setParameter("chargerId", chargerId);
        query.setParameter("startTime", startTime);
        query.setParameter("endTime", endTime);

        EnergyChargerIntervalForCharger response = new EnergyChargerIntervalForCharger();
        response.setEnergyList(query.getResultList());
        response.setCharger(chargerService.getCharger(chargerId));
        response.setStartTime(startTime);
        response.setEndTime(endTime);
        response.setSum(
                response.getEnergyList()
                        .stream()
                        .map(ChargerEnergyEntity::getValue)
                        .reduce(new BigDecimal(0), BigDecimal::add)
        );
        return response;
    }

    public EnergyChargerIntervalForVehicle getChargerEnergyDuringTimePeriodForVehicle(String vehicleId, Instant startTime, Instant endTime) {
        Query query = em.createNamedQuery(ChargerEnergyEntity.FIND_ALL_VEHICLE_ENERGY_DURING_TIME_PERIOD, ChargerEnergyEntity.class);
        query.setParameter("vehicleId", vehicleId);
        query.setParameter("startTime", startTime);
        query.setParameter("endTime", endTime);
        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleId);

        EnergyChargerIntervalForVehicle response = new EnergyChargerIntervalForVehicle();
        response.setVehicle(vehicle);
        response.setEnergyList(query.getResultList());
        response.setStartTime(startTime);
        response.setEndTime(endTime);
        response.setSum(
                response.getEnergyList()
                        .stream()
                        .map(ChargerEnergyEntity::getValue)
                        .reduce(new BigDecimal(0), BigDecimal::add)
        );
        return response;
    }

//    public EnergyChargerIntervalForUser getChargerEnergyDuringTimePeriodForUser(User user, Instant startTime, Instant endTime) {
//        EnergyChargerIntervalForUser data = new EnergyChargerIntervalForUser();
//        data.setUser(user);
//        List<EnergyChargerIntervalForCharger> chargerEnergyForIntervalList = new ArrayList<>();
//        data.setEnergyChargerList(chargerEnergyForIntervalList);
//        final BigDecimal[] energySum = {new BigDecimal(0)};
//
//        List<ChargerEntity> usersChargers = chargerService.getChargerForUser(user.getId());
//        usersChargers.forEach(charger -> {
//            EnergyChargerIntervalForCharger chargerEnergyForInterval = getChargerEnergyDuringTimePeriod(charger.getId(), startTime, endTime);
//            chargerEnergyForIntervalList.add(chargerEnergyForInterval);
//            energySum[0] = energySum[0].add(chargerEnergyForInterval.getSum());
//        });
//        data.setSum(energySum[0]);
//
//        return data;
//    }

    public List<EnergyChargerIntervalForVehicle> getChargerEnergyDuringTimePeriodForVehicles(List<Vehicle> vehiclesList, Instant startTime, Instant endTime, JetStreamMessage msg) {
        List<EnergyChargerIntervalForVehicle> energyList = new ArrayList<>();
        vehiclesList.forEach(vehicle -> {
            energyList.add(getChargerEnergyDuringTimePeriodForVehicle(vehicle.getId(), startTime, endTime));
            msg.inProgress();
        });
        return energyList;
    }

    public void sendDailyUserReportResponse(List<EnergyChargerIntervalForVehicle> energyChargerList) {
        energyChargerList.forEach(energyCharger -> {
            NatsMessage message = null;
            try {
                message = NatsMessage.builder()
                        .subject("charger.energy.dailyReportRes")
                        .data(SerDes.serialize(energyCharger))
                        .build();
            } catch (JsonProcessingException e) {
                LOG.log(LogLevel.ERROR, "Serialization error.", e);
                throw new RuntimeException(e);
            }
            jetStream.publishAsync(message);
            LOG.info("Sent data to subject " + "charger.energy.dailyReportRes");
        });
    }
}
