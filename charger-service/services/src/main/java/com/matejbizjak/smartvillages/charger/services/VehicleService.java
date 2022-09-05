package com.matejbizjak.smartvillages.charger.services;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.matejbizjak.smartvillages.charger.services.config.UrlProperties;
import com.matejbizjak.smartvillages.charger.services.restclients.VehicleApi;
import com.matejbizjak.smartvillages.vehicle.lib.v1.Vehicle;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.URL;
import java.util.List;

@ApplicationScoped
public class VehicleService {

    @Inject
    private UrlProperties urlProperties;

    private final Logger LOG = LogManager.getLogger(VehicleService.class.getSimpleName());

    public Vehicle getVehicle(String vehicleId) {
        try {
            VehicleApi vehicleApi = RestClientBuilder.newBuilder()
                    .baseUrl(new URL(urlProperties.getVehicleApi())) // TODO service discovery, fault tolerance etc.
                    .build(VehicleApi.class);

            Vehicle vehicle = vehicleApi.getVehicle(vehicleId);
            LOG.info("Received vehicle via REST client.");
            return vehicle;
        } catch (Exception e) {
            LOG.error(e);
            throw new RuntimeException(e);
        }
    }

}
