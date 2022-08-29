package com.matejbizjak.smartvillages.central.services;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.matejbizjak.smartvillages.central.services.restclients.VehicleApi;
import com.matejbizjak.smartvillages.vehicle.lib.v1.Vehicle;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.enterprise.context.ApplicationScoped;
import java.net.URL;
import java.util.List;

@ApplicationScoped
public class VehicleService {

    private final Logger LOG = LogManager.getLogger(VehicleService.class.getSimpleName());

    public List<Vehicle> getAll() {
        try {
            VehicleApi vehicleApi = RestClientBuilder.newBuilder()
                    .baseUrl(new URL("http://localhost:8085")) // TODO service discovery, fault tolerance etc.
                    .build(VehicleApi.class);

            List<Vehicle> allVehicles = vehicleApi.getAllVehicles();
            LOG.info("Received vehicles via REST client.");
            return allVehicles;
        } catch (Exception e) {
            LOG.error(e);
            throw new RuntimeException(e);
        }
    }

}
