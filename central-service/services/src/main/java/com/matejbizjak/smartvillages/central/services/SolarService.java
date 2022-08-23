package com.matejbizjak.smartvillages.central.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.nats.common.util.SerDes;
import com.kumuluz.ee.nats.core.annotations.NatsClient;
import com.kumuluz.ee.nats.jetstream.annotations.JetStreamProducer;
import com.matejbizjak.smartvillages.central.services.natsclients.SolarClient;
import io.nats.client.JetStream;
import io.nats.client.impl.NatsMessage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class SolarService {

    @Inject
    @JetStreamProducer(connection = "main-secure")
    private JetStream jetStream;

    @Inject
    @NatsClient
    private SolarClient solarClient;

    private final Logger LOG = LogManager.getLogger(SolarService.class.getSimpleName());

    public void startDailyEnergyReportProcess() {
        try {
            NatsMessage message = NatsMessage.builder()
                    .subject("solar.energy.dailyReportReq")
                    .data(SerDes.serialize(""))
                    .build();
            jetStream.publishAsync(message);
            LOG.info("Sent data to subject " + "solar.energy.dailyReportReq");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void changeSolarPositions() {
        solarClient.changeSolarPositions("");
        LOG.info("Sent data to subject " + "solar.sendNewPosition");
    }
}
