package com.matejbizjak.smartvillages.charger.api.v1.resources;

import com.matejbizjak.smartvillages.charger.services.subscribers.ChargerSubscriber;
import com.matejbizjak.smartvillages.vehicle.lib.v1.Vehicle;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/charger")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChargerResource {

    @Inject
    private ChargerSubscriber chargerSubscriber;

    @GET
    @Path("/status/{chargerId}")
//    @RolesAllowed(AuthRole.ADMIN)
    public Response getCurrentChargingState(@PathParam("chargerId") String chargerId) {
//            User user = userService.getUser(userId, getKeycloakSecurityContext().getTokenString());
        try {
            Vehicle vehicle = chargerSubscriber.getCurrentChargingState(chargerId);
            return Response.status(Response.Status.OK).entity(vehicle).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }
    }
}
