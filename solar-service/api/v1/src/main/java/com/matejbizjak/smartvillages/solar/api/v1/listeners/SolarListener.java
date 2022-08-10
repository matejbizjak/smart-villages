package com.matejbizjak.smartvillages.solar.api.v1.listeners;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.nats.core.annotations.NatsListener;
import com.kumuluz.ee.nats.core.annotations.Subject;
import com.kumuluz.ee.nats.jetstream.annotations.JetStreamListener;
import com.kumuluz.ee.nats.jetstream.util.JetStreamMessage;
import com.matejbizjak.smartvillages.solar.lib.v1.Energy;
import com.matejbizjak.smartvillages.solar.lib.v1.EnergySolarIntervalForSolar;
import com.matejbizjak.smartvillages.solar.lib.v1.EnergySolarIntervalForUser;
import com.matejbizjak.smartvillages.solar.services.EnergyService;
import com.matejbizjak.smartvillages.solar.services.PositionService;
import com.matejbizjak.smartvillages.solar.services.SolarService;
import com.matejbizjak.smartvillages.userlib.v1.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@ApplicationScoped
@NatsListener(connection = "secure")
public class SolarListener {

    @Inject
    private PositionService positionService;
    @Inject
    private EnergyService energyService;

    private final Logger LOG = LogManager.getLogger(SolarListener.class.getSimpleName());

    @Subject(value = "solar.sendNewPosition", queue = "solar")
    public void receiveNewPositionOrder(String value) {
        LOG.info("Received data from subject " + "solar.sendNewPosition");
        positionService.sendNewPositions();
    }

    @JetStreamListener(connection = "secure", subject = "solar.energy.new.*", queue = "solar")
    public void receiveEnergy(Energy energy, JetStreamMessage msg) {
        String solarId = msg.getSubject().split("\\.")[3];
        LOG.info("Received data from subject " + "solar.energy.new." + solarId);
        energyService.storeEnergy(energy, solarId);
    }

    @JetStreamListener(connection = "secure", subject = "solar.energy.dailyReportReq", queue = "solar")
    public void receiveDailyUserReportRequest(String payload, JetStreamMessage msg) {  // TODO payload je odveč. lahko popravim knjižnico tako, da sploh ne rabim dat 1. parametra funkciji?
        LOG.info("Received data from subject " + "solar.energy.dailyReportReq");
//        Instant startOfThePreviousDay = Instant.now().truncatedTo(ChronoUnit.DAYS).minus(1, ChronoUnit.DAYS);
//        Instant endOfThePreviousDay = Instant.now().truncatedTo(ChronoUnit.DAYS).minus(1, ChronoUnit.NANOS);
        Instant startOfThePreviousDay = Instant.now().truncatedTo(ChronoUnit.DAYS);  // TODO this is actually the current day - easier for testing
        Instant endOfThePreviousDay = Instant.now().truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS).minus(1, ChronoUnit.NANOS);
        System.out.println(startOfThePreviousDay);
        System.out.println(endOfThePreviousDay);  // TODO remove
        List<EnergySolarIntervalForSolar> energySolarList = energyService.getAllSolarEnergyDuringTimePeriod(startOfThePreviousDay
                , endOfThePreviousDay, msg);

        energyService.sendDailyUserReportResponse(energySolarList);
    }
}
