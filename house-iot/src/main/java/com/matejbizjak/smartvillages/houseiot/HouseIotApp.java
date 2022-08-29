package com.matejbizjak.smartvillages.houseiot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.matejbizjak.smartvillages.house.lib.v1.HouseEnergy;
import com.matejbizjak.smartvillages.libutils.SslUtil;
import io.nats.client.*;
import io.nats.client.api.PublishAck;
import io.nats.client.impl.NatsMessage;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HouseIotApp {

    private static final Logger LOG = LogManager.getLogger(HouseIotApp.class.getSimpleName());
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static void main(String[] args) {
        String houseId = args[0];
        String keyStorePassword = args[1];
        String trustStorePassword = args[2];

        SSLContext sslContext;
        try {
            sslContext = new SslUtil("certs/keystore.jks", keyStorePassword
                    , "certs/truststore.jks", trustStorePassword).createSslContext();
        } catch (Exception e) {
            LOG.error("Cannot create the SSL Context", e);
            throw new RuntimeException(e);
        }
        Options options = new Options.Builder()
                .server("tls://localhost:4222")
                .server("tls://localhost:4223")
                .server("tls://localhost:4224")
                .sslContext(sslContext)
                .build();
        try {
            Connection nc = Nats.connect(options);
            System.out.println(nc.getServerInfo());
            LOG.info(String.format("House %s was successfully connected to NATS server.", houseId));
            JetStream jetStream = nc.jetStream();

            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
            executorService.scheduleAtFixedRate(new HouseEnergyMeter(jetStream, "house.energy.new." + houseId)
                    , 0, 1, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOG.error("Cannot connect to NATS servers.", e);
            throw new RuntimeException(e);
        }
    }

    static class HouseEnergyMeter implements Runnable {

        private final JetStream jetStream;
        private final String subject;

        public HouseEnergyMeter(JetStream jetStream, String subject) {
            this.jetStream = jetStream;
            this.subject = subject;
        }

        @Override
        public void run() {
            NatsMessage natsMessage = NatsMessage.builder()
                    .subject(subject)
                    .data(readMeasurements())
                    .build();
            try {
                PublishAck publishAck = jetStream.publish(natsMessage);
                System.out.println(publishAck.getStream());
                LOG.info(String.format("Published JetStream message to subject %s.", subject));
            } catch (IOException | JetStreamApiException e) {
                LOG.error("Failed publishing JetStream message.", e);
                throw new RuntimeException(e);
            }
        }

        private byte[] readMeasurements() {
            HouseEnergy houseEnergy = new HouseEnergy();
            houseEnergy.setStartTime(Instant.now().truncatedTo(ChronoUnit.SECONDS));
            houseEnergy.setValue(generateRandomBigDecimalFromRange(new BigDecimal(-2000), new BigDecimal(-2700)));
            houseEnergy.setDuration(Duration.ofSeconds(1));

            try {
                return objectMapper.writeValueAsBytes(houseEnergy);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        public static BigDecimal generateRandomBigDecimalFromRange(BigDecimal min, BigDecimal max) {
            BigDecimal randomBigDecimal = min.add(BigDecimal.valueOf(Math.random()).multiply(max.subtract(min)));
            return randomBigDecimal.setScale(2, RoundingMode.HALF_UP);
        }
    }
}
