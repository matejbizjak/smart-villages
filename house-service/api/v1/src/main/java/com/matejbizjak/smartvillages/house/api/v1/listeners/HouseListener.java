package com.matejbizjak.smartvillages.house.api.v1.listeners;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.nats.jetstream.annotations.JetStreamListener;
import com.kumuluz.ee.nats.jetstream.util.JetStreamMessage;
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

    @JetStreamListener(connection = "main-secure", subject = "house.energy.dailyReportReq", queue = "house")
    public void receiveDailyUserReportRequest(String payload, JetStreamMessage msg) {  // TODO payload je odveč. lahko popravim knjižnico tako, da sploh ne rabim dat 1. parametra funkciji?
        LOG.info("Received data from subject " + "house.energy.dailyReportReq");
//        Instant startOfThePreviousDay = Instant.now().truncatedTo(ChronoUnit.DAYS).minus(1, ChronoUnit.DAYS);
//        Instant endOfThePreviousDay = Instant.now().truncatedTo(ChronoUnit.DAYS).minus(1, ChronoUnit.NANOS);
        Instant startOfThePreviousDay = Instant.now().truncatedTo(ChronoUnit.DAYS);  // TODO this is actually the current day - easier for testing
        Instant endOfThePreviousDay = Instant.now().truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS).minus(1, ChronoUnit.NANOS);
        System.out.println(startOfThePreviousDay);
        System.out.println(endOfThePreviousDay);  // TODO remove
        List<EnergyHouseIntervalForHouse> energyHouseList = energyService.getAllHouseEnergyDuringTimePeriod(startOfThePreviousDay, endOfThePreviousDay, msg);

        energyService.sendDailyUserReportResponse(energyHouseList);
    }

    @JetStreamListener(connection = "main-secure", subject = "house.energy.new.*", queue = "house")
    public void receiveEnergy(HouseEnergy houseEnergy, JetStreamMessage msg) {
        String houseId = msg.getSubject().split("\\.")[3];
        LOG.info("Received data from subject " + "house.energy.new." + houseId);
        energyService.storeEnergy(houseEnergy, houseId);
    }

}
