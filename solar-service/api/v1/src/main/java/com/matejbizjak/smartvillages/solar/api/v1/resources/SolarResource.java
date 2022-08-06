package com.matejbizjak.smartvillages.solar.api.v1.resources;

import com.kumuluz.ee.nats.common.annotations.ConsumerConfig;
import com.kumuluz.ee.nats.jetstream.annotations.JetStreamListener;
import com.kumuluz.ee.nats.jetstream.util.JetStreamMessage;
import com.matejbizjak.smartvillages.solar.lib.v1.Energy;
import com.matejbizjak.smartvillages.solar.persistence.Solar;
import com.matejbizjak.smartvillages.solar.services.SolarService;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/solar")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
//@Secure
public class SolarResource {

    @Inject
    private SolarService solarService;

//    @Inject
//    @JetStreamSubscriber(connection = "secure", subject = "solar.energy.*", durable = "solar-energy-sub")
//    @ConsumerConfig(name = "newMessages")
//    private JetStreamSubscription solarEnergySubscription;

//    @Context
//    private SecurityContext securityContext;

//    private KeycloakSecurityContext getKeycloakSecurityContext() {
//        if (securityContext != null && securityContext.getUserPrincipal() instanceof KeycloakPrincipal) {
//            KeycloakPrincipal principal = ((KeycloakPrincipal) securityContext.getUserPrincipal());
//            return principal.getKeycloakSecurityContext();
//        } else {
//            return null;
//        }
//    }

    @GET
    @Path("/{solarId}")
//    @RolesAllowed(AuthRole.ADMIN)
    public Response getSolar(@PathParam("solarId") String solarId) {
//            Solar user = userService.getUser(userId, getKeycloakSecurityContext().getTokenString());
        try {
            Solar solar = solarService.getSolar(solarId);
            return Response.status(Response.Status.OK).entity(solar).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }
    }

    @JetStreamListener(connection = "secure", subject = "solar.energy.*", queue = "solar_energy_listeners")
    @ConsumerConfig(name = "newMessages")
    public void receive(Energy energy, JetStreamMessage msg) {
        System.out.println(energy.getWatt());
//        solarService.storeEnergy(energy, msg.getSubject().split("\\.")[2]);
    }
}
