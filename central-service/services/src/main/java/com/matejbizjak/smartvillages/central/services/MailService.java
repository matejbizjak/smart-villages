package com.matejbizjak.smartvillages.central.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.logs.enums.LogLevel;
import com.kumuluz.ee.nats.common.util.SerDes;
import com.kumuluz.ee.nats.jetstream.annotations.JetStreamProducer;
import com.matejbizjak.smartvillages.central.lib.v1.TotalUserEnergyInterval;
import io.nats.client.JetStream;
import io.nats.client.impl.NatsMessage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MailService {

    private final Logger LOG = LogManager.getLogger(MailService.class.getSimpleName());

    @Inject
    @JetStreamProducer(connection = "secure")
    private JetStream jetStream;

    public void sendEnergyDailyMail(TotalUserEnergyInterval data) {
        try {
            NatsMessage message = NatsMessage.builder()
                    .subject("mail.dailyUsage")
                    .data(SerDes.serialize(data))
                    .build();
            jetStream.publishAsync(message);
            LOG.info("Sent data to subject " + "mail.dailyUsage");
        } catch (JsonProcessingException e) {
            LOG.log(LogLevel.ERROR, "Serialization error.", e);
            throw new RuntimeException(e);
        }
    }
}
