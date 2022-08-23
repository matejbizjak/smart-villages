package com.matejbizjak.smartvillages.vehicle.api.v1.resources;

import com.matejbizjak.smartvillages.vehicle.persistence.VehicleEntity;
import com.matejbizjak.smartvillages.vehicle.service.VehicleService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/vehicle")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
//@Secure
public class VehicleResource {

    @Inject
    private VehicleService vehicleService;

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
//    @RolesAllowed(AuthRole.ADMIN)
    public Response getAllVehicle() {
//            Solar user = userService.getUser(userId, getKeycloakSecurityContext().getTokenString());
        try {
            List<VehicleEntity> vehicleEntityList = vehicleService.getAll();
            return Response.status(Response.Status.OK).entity(vehicleEntityList).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }
    }

    @GET
    @Path("/{vehicleId}")
//    @RolesAllowed(AuthRole.ADMIN)
    public Response getVehicle(@PathParam("vehicleId") String vehicleId) {
//            Solar user = userService.getUser(userId, getKeycloakSecurityContext().getTokenString());
        try {
            VehicleEntity vehicleEntity = vehicleService.get(vehicleId);
            return Response.status(Response.Status.OK).entity(vehicleEntity).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }
    }

    @GET
    @Path("/user/{userId}")
//    @RolesAllowed(AuthRole.ADMIN)
    public Response getVehiclesByUser(@PathParam("userId") String userId) {
//            Solar user = userService.getUser(userId, getKeycloakSecurityContext().getTokenString());
        try {
            List<VehicleEntity> vehicleEntityList = vehicleService.getByUser(userId);
            return Response.status(Response.Status.OK).entity(vehicleEntityList).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }
    }
}
