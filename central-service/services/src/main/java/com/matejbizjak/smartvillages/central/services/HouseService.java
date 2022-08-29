package com.matejbizjak.smartvillages.central.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.nats.common.util.SerDes;
import com.kumuluz.ee.nats.jetstream.annotations.JetStreamProducer;
import io.nats.client.JetStream;
import io.nats.client.impl.NatsMessage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class HouseService {

    @Inject
    @JetStreamProducer(connection = "main-secure")
    private JetStream jetStream;

    private final Logger LOG = LogManager.getLogger(HouseService.class.getSimpleName());

    public void startDailyEnergyReportProcess() {
        try {
            NatsMessage message = NatsMessage.builder()
                    .subject("house.energy.dailyReportReq")
                    .data(SerDes.serialize(""))
                    .build();
            jetStream.publishAsync(message);
            LOG.info("Sent data to subject " + "house.energy.dailyReportReq");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
