package com.matejbizjak.smartvillages.solar.api.v1.resources;

import com.kumuluz.ee.nats.core.annotations.NatsListener;
import com.kumuluz.ee.nats.core.annotations.Subject;
import com.kumuluz.ee.nats.jetstream.annotations.JetStreamListener;
import com.kumuluz.ee.nats.jetstream.util.JetStreamMessage;
import com.matejbizjak.smartvillages.solar.lib.v1.Energy;
import com.matejbizjak.smartvillages.solar.services.EnergyService;
import com.matejbizjak.smartvillages.solar.services.PositionService;
import com.matejbizjak.smartvillages.solar.services.SolarService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
@NatsListener(connection = "secure")
public class SolarListener {

    @Inject
    private PositionService positionService;
    @Inject
    private EnergyService energyService;

    @JetStreamListener(connection = "secure", subject = "solar.energy.*", queue = "solar_energy_listeners")
    public void receiveEnergy(Energy energy, JetStreamMessage msg) {
        energyService.storeEnergy(energy, msg.getSubject().split("\\.")[2]);
    }

    @Subject(value = "solar.sendNewPosition", queue = "solar_send_new_position_listeners")
    public void receiveNewPositionOrder(String value) {
        positionService.sendNewPositions();
    }
}
