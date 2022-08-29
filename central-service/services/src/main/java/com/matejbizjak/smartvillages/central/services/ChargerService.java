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
public class ChargerService {

    @Inject
    @JetStreamProducer(connection = "main-secure")
    private JetStream jetStream;

    @Inject
    private VehicleService vehicleService;

    private final Logger LOG = LogManager.getLogger(ChargerService.class.getSimpleName());

    public void startDailyEnergyReportProcess() {
        try {
            NatsMessage message = NatsMessage.builder()
                    .subject("charger.energy.dailyReportReq")
                    .data(SerDes.serialize(vehicleService.getAll()))
                    .build();
            jetStream.publishAsync(message);
            LOG.info("Sent data to subject " + "charger.energy.dailyReportReq");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
