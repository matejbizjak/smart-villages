package com.matejbizjak.smartvillages.house.api.v1.listeners;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.nats.jetstream.annotations.JetStreamListener;
import com.kumuluz.ee.nats.jetstream.wrappers.JetStreamMessage;
import com.matejbizjak.smartvillages.house.lib.v1.EnergyHouseIntervalForHouse;
import com.matejbizjak.smartvillages.house.lib.v1.HouseEnergy;
import com.matejbizjak.smartvillages.house.services.EnergyService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@ApplicationScoped
public class HouseListener {

    @Inject
    private EnergyService energyService;

    private final Logger LOG = LogManager.getLogger(HouseListener.class.getSimpleName());

    @JetStreamListener(connection = "main-secure", stream = "house", subject = "house.energy.dailyReportReq", queue = "house", durable = "energyReportReqConsumer")
    public void receiveDailyUserReportRequest(String payload, JetStreamMessage msg) {
        LOG.info("Received data from subject " + "house.energy.dailyReportReq");
//        Instant startOfThePreviousDay = Instant.now().truncatedTo(ChronoUnit.DAYS).minus(1, ChronoUnit.DAYS);
//        Instant endOfThePreviousDay = Instant.now().truncatedTo(ChronoUnit.DAYS).minus(1, ChronoUnit.NANOS);
        Instant startOfThePreviousDay = Instant.now().truncatedTo(ChronoUnit.DAYS);  // TODO this is actually the current day - easier for testing
        Instant endOfThePreviousDay = Instant.now().truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS).minus(1, ChronoUnit.NANOS);
        List<EnergyHouseIntervalForHouse> energyHouseList = energyService.getAllHouseEnergyDuringTimePeriod(startOfThePreviousDay, endOfThePreviousDay, msg);

        energyService.sendDailyUserReportResponse(energyHouseList);
    }

    @JetStreamListener(connection = "main-secure", stream = "house", subject = "house.energy.new.*", queue = "house", durable = "energyConsumer")
    public void receiveEnergy(HouseEnergy houseEnergy, JetStreamMessage msg) {
        String houseId = msg.getSubject().split("\\.")[3];
        LOG.info("Received data from subject " + "house.energy.new." + houseId);
        energyService.storeEnergy(houseEnergy, houseId);
    }

}
