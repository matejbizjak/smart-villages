package com.matejbizjak.smartvillages.charger.services.restclients;

import com.matejbizjak.smartvillages.vehicle.lib.v1.Vehicle;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.Dependent;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/v1/vehicle")
@RegisterRestClient
@Dependent
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface VehicleApi {

    @GET
    @Path("/{vehicleId}")
    Vehicle getVehicle(@PathParam("vehicleId") String vehicleId);
}
