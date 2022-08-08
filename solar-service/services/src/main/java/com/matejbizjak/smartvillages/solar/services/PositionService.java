package com.matejbizjak.smartvillages.solar.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.logs.enums.LogLevel;
import com.kumuluz.ee.nats.common.util.SerDes;
import com.kumuluz.ee.nats.jetstream.annotations.JetStreamProducer;
import com.matejbizjak.smartvillages.solar.persistence.SolarEntity;
import io.nats.client.JetStream;
import io.nats.client.impl.NatsMessage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.awt.*;
import java.math.BigDecimal;
import java.time.Instant;

@ApplicationScoped
@Transactional
public class PositionService {

    @Inject
    private SolarService solarService;
    @Inject
    @JetStreamProducer
    private JetStream jetStream;

    @PersistenceContext(unitName = "main-jpa-unit")
    private EntityManager em;

    private final Logger LOG = LogManager.getLogger(PositionService.class.getSimpleName());

    public void sendNewPositions() {
        solarService.getAll().forEach(solarEntity -> {
            try {
                NatsMessage message = NatsMessage.builder()
                        .subject("solar.position." + solarEntity.getId())
                        .data(SerDes.serialize(calculateNewPosition(solarEntity)))
                        .build();
                jetStream.publishAsync(message);
            } catch (JsonProcessingException e) {
                LOG.log(LogLevel.ERROR, "Serialization error.", e);
                throw new RuntimeException(e);
            }
        });
    }

    private Point calculateNewPosition(SolarEntity solarEntity) {
        Instant currentTime = Instant.now();
        BigDecimal locationX = solarEntity.getLocationX();
        BigDecimal locationY = solarEntity.getLocationY();

        return new Point(getRandomNumber(0, 180), getRandomNumber(0, 90));
    }

    private int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
