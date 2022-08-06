package com.matejbizjak.smartvillages.user.api.v1.resouces;

import com.kumuluz.ee.security.annotations.Secure;
import com.matejbizjak.smartvillages.user.api.v1.config.AuthRole;
import com.matejbizjak.smartvillages.user.persistence.User;
import com.matejbizjak.smartvillages.user.services.UserService;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.net.MalformedURLException;
import java.util.List;

@Path("/user")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Secure
public class UserResource {

    @Inject
    private UserService userService;

    @Context
    private SecurityContext securityContext;

    private KeycloakSecurityContext getKeycloakSecurityContext() {
        if (securityContext != null && securityContext.getUserPrincipal() instanceof KeycloakPrincipal) {
            KeycloakPrincipal principal = ((KeycloakPrincipal) securityContext.getUserPrincipal());
            return principal.getKeycloakSecurityContext();
        } else {
            return null;
        }
    }

    @GET
    @RolesAllowed(AuthRole.ADMIN)
    @Retry
    public Response getUsers() {
        try {
            List<User> users = userService.getUsers(getKeycloakSecurityContext().getTokenString());
            return Response.status(Response.Status.OK).entity(users).build();
        } catch (MalformedURLException e) {
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/{userId}")
    @RolesAllowed(AuthRole.ADMIN)
    @Retry
    public Response getUser(@PathParam("userId") String userId) {
        try {
            User user = userService.getUser(userId, getKeycloakSecurityContext().getTokenString());
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.status(Response.Status.OK).entity(user).build();
        } catch (MalformedURLException e) {
            return Response.serverError().build();
        }
    }
}
