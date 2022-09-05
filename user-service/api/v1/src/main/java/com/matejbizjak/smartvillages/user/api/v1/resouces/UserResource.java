package com.matejbizjak.smartvillages.user.api.v1.resouces;

import com.matejbizjak.smartvillages.user.services.UserService;
import com.matejbizjak.smartvillages.userlib.v1.User;
import org.eclipse.microprofile.faulttolerance.Retry;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/user")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
//@Secure
public class UserResource {

    @Inject
    private UserService userService;

//    @Context
//    private SecurityContext securityContext;
//
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
    @Retry
    public Response getUsers() {
//        try {
//            List<User> users = userService.getUsers(getKeycloakSecurityContext().getTokenString());

        List<User> users = new ArrayList<>();
        users.add(new User("9349cf54-1946-4915-be7e-7decb9090e8e", "user1", "user1@mail.com", "Uporabnik", "Ena", "051000001"));
        users.add(new User("8349cf54-1946-4915-be7e-7decb9090e8e", "user2", "user2@mail.com", "Uporabnik", "Dva", "051000002"));
        return Response.status(Response.Status.OK).entity(users).build();
//        }
//        catch (MalformedURLException e) {
//            return Response.serverError().build();
//        }
    }

    @GET
    @Path("/{userId}")
//    @RolesAllowed(AuthRole.ADMIN)
    @Retry
    public Response getUser(@PathParam("userId") String userId) {
//        try {
//            User user = userService.getUser(userId, getKeycloakSecurityContext().getTokenString());
//            if (user == null) {
//                return Response.status(Response.Status.NOT_FOUND).build();
//            }
//            return Response.status(Response.Status.OK).entity(user).build();
//        } catch (MalformedURLException e) {
//            return Response.serverError().build();
//        }
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }
}
