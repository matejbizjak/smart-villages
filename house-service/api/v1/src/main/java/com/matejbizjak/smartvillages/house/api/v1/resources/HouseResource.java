package com.matejbizjak.smartvillages.house.api.v1.resources;

import com.matejbizjak.smartvillages.house.lib.v1.EnergyHouseIntervalForHouse;
import com.matejbizjak.smartvillages.house.persistence.HouseEntity;
import com.matejbizjak.smartvillages.house.services.EnergyService;
import com.matejbizjak.smartvillages.house.services.HouseService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.time.format.DateTimeParseException;

@Path("/house")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
//@Secure
public class HouseResource {

    @Inject
    private HouseService houseService;
    @Inject
    private EnergyService energyService;

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
    @Path("/{houseId}")
//    @RolesAllowed(AuthRole.ADMIN)
    public Response getHouse(@PathParam("houseId") String houseId) {
//            House user = userService.getUser(userId, getKeycloakSecurityContext().getTokenString());
        try {
            HouseEntity houseEntity = houseService.getHouse(houseId);
            return Response.status(Response.Status.OK).entity(houseEntity).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }
    }

    @GET
    @Path("/energy/{houseId}")
    public Response getHouseEnergy(@PathParam("houseId") String houseId, @QueryParam("startTime") String startTimeString
            , @QueryParam("endTime") String endTimeString) {
        try {
            Instant startTime = Instant.parse(startTimeString);
            Instant endTime = Instant.parse(endTimeString);
            EnergyHouseIntervalForHouse energyDuringTimePeriod = energyService.getHouseEnergyDuringTimePeriod(houseId, startTime, endTime);
            return Response.status(Response.Status.OK).entity(energyDuringTimePeriod).build();
        } catch (DateTimeParseException dtpe) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Cannot parse the provided dates.").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }
    }
}
