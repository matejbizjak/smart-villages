package com.matejbizjak.smartvillages.solar.api.v1.listeners;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.nats.core.annotations.NatsListener;
import com.kumuluz.ee.nats.core.annotations.Subject;
import com.kumuluz.ee.nats.jetstream.annotations.JetStreamListener;
import com.kumuluz.ee.nats.jetstream.wrappers.JetStreamMessage;
import com.matejbizjak.smartvillages.solar.lib.v1.SolarEnergy;
import com.matejbizjak.smartvillages.solar.lib.v1.EnergySolarIntervalForSolar;
import com.matejbizjak.smartvillages.solar.services.EnergyService;
import com.matejbizjak.smartvillages.solar.services.PositionService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@ApplicationScoped
@NatsListener(connection = "main-secure")
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

    @JetStreamListener(connection = "main-secure", stream = "solar", subject = "solar.energy.dailyReportReq", queue = "solar", durable = "energyReportReqConsumer")
    public void receiveDailyUserReportRequest(String payload, JetStreamMessage msg) {
        LOG.info("Received data from subject " + "solar.energy.dailyReportReq");
//        Instant startOfThePreviousDay = Instant.now().truncatedTo(ChronoUnit.DAYS).minus(1, ChronoUnit.DAYS);
//        Instant endOfThePreviousDay = Instant.now().truncatedTo(ChronoUnit.DAYS).minus(1, ChronoUnit.NANOS);
        Instant startOfThePreviousDay = Instant.now().truncatedTo(ChronoUnit.DAYS);  // TODO this is actually the current day - easier for testing
        Instant endOfThePreviousDay = Instant.now().truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS).minus(1, ChronoUnit.NANOS);
        List<EnergySolarIntervalForSolar> energySolarList = energyService.getAllSolarEnergyDuringTimePeriod(startOfThePreviousDay
                , endOfThePreviousDay, msg);

        energyService.sendDailyUserReportResponse(energySolarList);
    }

    @JetStreamListener(connection = "leaf-secure", stream = "solar", subject = "solar.energy.new.*", queue = "solar", durable = "energyConsumer")
    public void receiveEnergy(SolarEnergy solarEnergy, JetStreamMessage msg) {
        String solarId = msg.getSubject().split("\\.")[3];
        LOG.info("Received data from subject " + "solar.energy.new." + solarId);
        energyService.storeEnergy(solarEnergy, solarId);
    }
}
