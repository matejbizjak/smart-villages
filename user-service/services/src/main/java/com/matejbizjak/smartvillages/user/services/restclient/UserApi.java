package com.matejbizjak.smartvillages.user.services.restclient;

import com.matejbizjak.smartvillages.user.persistence.User;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.List;

@RegisterRestClient
public interface UserApi {

    @GET
    @Path("/users")
    List<User> getUsers(@HeaderParam("Authorization") String authorization);

    @GET
    @Path("/users/{userId}")
    User getUser(@HeaderParam("Authorization") String authorization, @PathParam("userId") String userId);
}
