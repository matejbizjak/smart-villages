package com.matejbizjak.smartvillages.user.services.restclient;

import com.matejbizjak.smartvillages.userlib.v1.User;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.Dependent;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@RegisterRestClient
@Dependent
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface UserApi {

    @GET
    @Path("/users")
    List<User> getUsers(@HeaderParam("Authorization") String authorization);

    @GET
    @Path("/users/{userId}")
    User getUser(@HeaderParam("Authorization") String authorization, @PathParam("userId") String userId);
}
