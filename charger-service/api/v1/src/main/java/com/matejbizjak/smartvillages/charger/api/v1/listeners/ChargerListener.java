package com.matejbizjak.smartvillages.charger.api.v1.listeners;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.nats.jetstream.annotations.JetStreamListener;
import com.kumuluz.ee.nats.jetstream.wrappers.JetStreamMessage;
import com.matejbizjak.smartvillages.charger.lib.v1.ChargerEnergy;
import com.matejbizjak.smartvillages.charger.lib.v1.EnergyChargerIntervalForVehicle;
import com.matejbizjak.smartvillages.charger.services.EnergyService;
import com.matejbizjak.smartvillages.vehicle.lib.v1.Vehicle;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@ApplicationScoped
public class ChargerListener {

    @Inject
    private EnergyService energyService;

    private final Logger LOG = LogManager.getLogger(ChargerListener.class.getSimpleName());

    @JetStreamListener(connection = "main-secure", stream = "charger", subject = "charger.energy.dailyReportReq", queue = "charger", durable = "energyReportReqConsumer")
    public void receiveDailyUserReportRequest(List<Vehicle> vehicleList, JetStreamMessage msg) {
        LOG.info("Received data from subject " + "charger.energy.dailyReportReq");
//        Instant startOfThePreviousDay = Instant.now().truncatedTo(ChronoUnit.DAYS).minus(1, ChronoUnit.DAYS);
//        Instant endOfThePreviousDay = Instant.now().truncatedTo(ChronoUnit.DAYS).minus(1, ChronoUnit.NANOS);
        Instant startOfThePreviousDay = Instant.now().truncatedTo(ChronoUnit.DAYS);  // TODO this is actually the current day - easier for testing
        Instant endOfThePreviousDay = Instant.now().truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS).minus(1, ChronoUnit.NANOS);
        List<EnergyChargerIntervalForVehicle> energyForVehicle = energyService.getChargerEnergyDuringTimePeriodForVehicles(vehicleList, startOfThePreviousDay, endOfThePreviousDay, msg);

        energyService.sendDailyUserReportResponse(energyForVehicle);
    }

    @JetStreamListener(connection = "main-secure", stream = "charger", subject = "charger.energy.new.*", queue = "charger", durable = "energyConsumer")
    public void receiveEnergy(ChargerEnergy chargerEnergy, JetStreamMessage msg) {
        String chargerId = msg.getSubject().split("\\.")[3];
        LOG.info("Received data from subject " + "charger.energy.new." + chargerId);
        energyService.storeEnergy(chargerEnergy, chargerId);
    }

}
