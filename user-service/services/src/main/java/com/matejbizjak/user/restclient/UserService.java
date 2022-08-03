package com.matejbizjak.user.restclient;

import com.matejbizjak.user.User;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.List;

@RegisterRestClient
public interface UserService {

    @GET
    @Path("/users")
    List<User> getUsers(@HeaderParam("Authorization") String authorization);

    @GET
    @Path("/users/{userId}")
    User getUser(@HeaderParam("Authorization") String authorization, @PathParam("userId") String userId);
}
