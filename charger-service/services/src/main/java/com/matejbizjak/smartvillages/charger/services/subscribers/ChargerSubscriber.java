package com.matejbizjak.smartvillages.charger.services.subscribers;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.nats.common.util.SerDes;
import com.kumuluz.ee.nats.jetstream.annotations.JetStreamProducer;
import com.matejbizjak.smartvillages.charger.services.VehicleService;
import com.matejbizjak.smartvillages.vehicle.lib.v1.Vehicle;
import io.nats.client.*;
import io.nats.client.api.ConsumerConfiguration;
import io.nats.client.api.DeliverPolicy;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

@ApplicationScoped
public class ChargerSubscriber {

    @Inject
    private VehicleService vehicleService;
    @Inject
    @JetStreamProducer(connection = "main-secure")
    private JetStream jetStream;

//    // TODO ne morem nastaviti dinamičnih subjectov na ta način
//    @Inject
//    @JetStreamSubscriber(connection = "main-secure", subject = "charger.plugged.*", durable = "un/plugged_vehicle", bind = true)
//    @ConsumerConfig(name = "last")
//    private JetStreamSubscription pluggedSubscription;

    private final Logger LOG = LogManager.getLogger(ChargerSubscriber.class.getSimpleName());

    private static final String CHARGING_START = "start";
    private static final String CHARGING_END = "end";

    public Vehicle getCurrentChargingState(String chargerId) {
        ConsumerConfiguration consumerConfiguration = ConsumerConfiguration.builder()
                .deliverPolicy(DeliverPolicy.Last)
                .build();
        PullSubscribeOptions pullOptions = PullSubscribeOptions.builder()
                .configuration(consumerConfiguration)
                .build();
        try {
            JetStreamSubscription subscription = jetStream.subscribe(String.format("charger.charging.%s.*", chargerId), pullOptions);
            Message message = subscription.fetch(1, Duration.ofSeconds(1)).get(0);
            String status = message.getSubject().split("\\.")[3];
            message.ack();
            if (Objects.equals(status, CHARGING_START)) {
                Vehicle vehicle = SerDes.deserialize(message.getData(), Vehicle.class);
                return vehicleService.getVehicle(vehicle.getId());
            } else if (Objects.equals(status, CHARGING_END)) {
                return null;
            } else {
                LOG.error("Unknown charging status.");
                throw new IllegalStateException();
            }
        } catch (IOException | JetStreamApiException e) {
            throw new RuntimeException(e);
        }
    }
}
