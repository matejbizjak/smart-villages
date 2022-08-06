package com.matejbizjak.smartvillages.solariot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.matejbizjak.smartvillages.solar.lib.v1.Energy;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SolarIotApp {
    static ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static void main(String[] args) {
        String solarId = args[0];
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

            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
            executorService.scheduleAtFixedRate(new EnergyMeter(mqttClient, "solar/energy/" + solarId)
                    , 0, 1, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static class EnergyMeter implements Runnable {

        private final MqttClient mqttClient;
        private final String topic;

        public EnergyMeter(MqttClient mqttClient, String topic) {
            this.mqttClient = mqttClient;
            this.topic = topic;
        }

        public void run() {
            try {
                mqttClient.publish(topic, readMeasurements());
                System.out.println("Published message to topic " + topic);
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        }

        private MqttMessage readMeasurements() {
            Energy energy = new Energy();
            energy.setStartTime(Instant.now().truncatedTo(ChronoUnit.SECONDS));
            energy.setWatt(generateRandomBigDecimalFromRange(new BigDecimal(1), new BigDecimal(5)));
            energy.setDuration(Duration.ofSeconds(1));

            try {
                return new MqttMessage(objectMapper.writeValueAsBytes(energy));
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
