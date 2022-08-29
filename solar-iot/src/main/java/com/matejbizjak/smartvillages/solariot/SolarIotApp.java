package com.matejbizjak.smartvillages.solariot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.matejbizjak.smartvillages.libutils.SslUtil;
import com.matejbizjak.smartvillages.solar.lib.v1.SolarEnergy;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SolarIotApp {

    private static final Logger LOG = LogManager.getLogger(SolarIotApp.class.getSimpleName());
    private static String solarId;
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static void main(String[] args) {
        solarId = args[0];
        String keyStorePassword = args[1];
        String trustStorePassword = args[2];

        String publisherId = "solar_iot_" + solarId;
        try {
            MqttClient mqttClient = new MqttClient("ssl://localhost:1883", publisherId);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(false);
            options.setConnectionTimeout(10);
            options.setSocketFactory(new SslUtil("certs/keystore.jks", keyStorePassword
                    , "certs/truststore.jks", trustStorePassword).createSocketFactory());

            mqttClient.connect(options);
            LOG.info(String.format("Solar %s was successfully connected to MQTT server.", solarId));

            mqttClient.subscribe("solar/position/" + solarId, (topic, msg) -> {
                Point newPosition = objectMapper.readValue(msg.getPayload(), Point.class);
                changePosition(newPosition);
            });
            LOG.info(String.format("Subscribed to MQTT topic %s.", "solar/position/" + solarId));

            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
            executorService.scheduleAtFixedRate(new SolarEnergyMeter(mqttClient, "solar/energy/new/" + solarId)
                    , 0, 15, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void changePosition(Point newPosition) {
        LOG.info(String.format("Changing position of solar %s to (%s, %s).", solarId, newPosition.x, newPosition.y));
    }

    static class SolarEnergyMeter implements Runnable {

        private final MqttClient mqttClient;
        private final String topic;

        public SolarEnergyMeter(MqttClient mqttClient, String topic) {
            this.mqttClient = mqttClient;
            this.topic = topic;
        }

        @Override
        public void run() {
            try {
                mqttClient.publish(topic, readMeasurements());
                LOG.info(String.format("Published MQTT message to topic %s.", topic));
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        }

        private MqttMessage readMeasurements() {
            SolarEnergy SolarEnergy = new SolarEnergy();
            SolarEnergy.setStartTime(Instant.now().truncatedTo(ChronoUnit.SECONDS));
            SolarEnergy.setValue(generateRandomBigDecimalFromRange(new BigDecimal(180), new BigDecimal(190)));
            SolarEnergy.setDuration(Duration.ofSeconds(1));

            try {
                return new MqttMessage(objectMapper.writeValueAsBytes(SolarEnergy));
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
