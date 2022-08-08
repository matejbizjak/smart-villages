package com.matejbizjak.smartvillages.solar.api.v1.resources;

import com.matejbizjak.smartvillages.solar.persistence.SolarEntity;
import com.matejbizjak.smartvillages.solar.services.EnergyService;
import com.matejbizjak.smartvillages.solar.services.SolarService;
import com.matejbizjak.smartvillages.solar.services.responses.EnergyResponse;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.time.format.DateTimeParseException;

@Path("/solar")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
//@Secure
public class SolarResource {

    @Inject
    private SolarService solarService;
    @Inject
    private EnergyService energyService;

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
            SolarEntity solarEntity = solarService.getSolar(solarId);
            return Response.status(Response.Status.OK).entity(solarEntity).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }
    }

    @GET
    @Path("/energy/{solarId}")
    public Response getSolarEnergy(@PathParam("solarId") String solarId, @QueryParam("startTime") String startTimeString
            , @QueryParam("endTime") String endTimeString) {
        try {
            Instant startTime = Instant.parse(startTimeString);
            Instant endTime = Instant.parse(endTimeString);
            EnergyResponse energyDuringTimePeriod = energyService.getSolarEnergyDuringTimePeriod(solarId, startTime, endTime);
            return Response.status(Response.Status.OK).entity(energyDuringTimePeriod).build();
        } catch (DateTimeParseException dtpe) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Cannot parse the provided dates.").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }
    }
}
