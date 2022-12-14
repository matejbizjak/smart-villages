package com.matejbizjak.smartvillages.central.services.restclients;

import com.matejbizjak.smartvillages.vehicle.lib.v1.Vehicle;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.Dependent;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/v1/vehicle")
@RegisterRestClient
@Dependent
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface VehicleApi {

    @GET
    List<Vehicle> getAllVehicles();
}
