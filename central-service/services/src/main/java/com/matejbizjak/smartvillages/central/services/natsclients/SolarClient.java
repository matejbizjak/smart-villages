package com.matejbizjak.smartvillages.central.services.natsclients;

import com.kumuluz.ee.nats.core.annotations.RegisterNatsClient;
import com.kumuluz.ee.nats.core.annotations.Subject;

@RegisterNatsClient(connection = "main-secure")
public interface SolarClient {

    @Subject(value = "solar.sendNewPosition")
    void changeSolarPositions(String msg);
}
