package com.matejbizjak.smartvillages.chargeriot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.matejbizjak.smartvillages.charger.lib.v1.ChargerEnergy;
import com.matejbizjak.smartvillages.libutils.SslUtil;
import com.matejbizjak.smartvillages.vehicle.lib.v1.Vehicle;
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

public class ChargerIotApp {

    private static final Logger LOG = LogManager.getLogger(ChargerIotApp.class.getSimpleName());
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static void main(String[] args) {
        String chargerId = args[0];
        String keyStorePassword = args[1];
        String trustStorePassword = args[2];

        Vehicle vehicle = new Vehicle();
        vehicle.setId("520cb7af-b986-4415-ba90-ba3c1a589333");

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
            LOG.info(String.format("Charger %s was successfully connected to NATS server.", chargerId));
            JetStream jetStream = nc.jetStream();

            sendChargingStartMessage(jetStream, chargerId, vehicle);
            // TODO be able to change state - stop charging, new car etc.

            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
            executorService.scheduleAtFixedRate(new ChargerEnergyMeter(jetStream, "charger.energy.new." + chargerId, vehicle)
                    , 0, 15, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOG.error("Cannot connect to NATS servers.", e);
            throw new RuntimeException(e);
        }
    }

    private static void sendChargingStartMessage(JetStream jetStream, String chargerId, Vehicle vehicle) {
        try {
            NatsMessage natsMessage = NatsMessage.builder()
                    .subject(String.format("charger.charging.%s.start", chargerId))
                    .data(objectMapper.writeValueAsBytes(vehicle))
                    .build();
            jetStream.publish(natsMessage);
            LOG.info("Charging start notice was sent.");
        } catch (JetStreamApiException | IOException e) {
            LOG.error(e);
            throw new RuntimeException(e);
        }
    }

    static class ChargerEnergyMeter implements Runnable {

        private final JetStream jetStream;
        private final String subject;
        private final Vehicle vehicle;

        public ChargerEnergyMeter(JetStream jetStream, String subject, Vehicle vehicle) {
            this.jetStream = jetStream;
            this.subject = subject;
            this.vehicle = vehicle;
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
            ChargerEnergy chargerEnergy = new ChargerEnergy();
            chargerEnergy.setStartTime(Instant.now().truncatedTo(ChronoUnit.SECONDS));
            chargerEnergy.setValue(generateRandomBigDecimalFromRange(new BigDecimal(-800), new BigDecimal(-1900)));
            chargerEnergy.setDuration(Duration.ofSeconds(1));
            chargerEnergy.setVehicle(vehicle);

            try {
                return objectMapper.writeValueAsBytes(chargerEnergy);
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
