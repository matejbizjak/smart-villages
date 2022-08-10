package com.matejbizjak.smartvillages.user.services;

import com.matejbizjak.smartvillages.user.services.config.ConfigProperties;
import com.matejbizjak.smartvillages.user.services.restclient.UserApi;
import com.matejbizjak.smartvillages.userlib.v1.User;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@ApplicationScoped
public class UserService {

    @Inject
    private ConfigProperties config;

    public List<User> getUsers(String token) throws MalformedURLException {
        URL url = new URL(config.getKeycloakUrl() + "/admin/realms/" + config.getKeycloakRealm());
        UserApi userRestClient = RestClientBuilder.newBuilder().baseUrl(url).build(UserApi.class);
        String auth = "Bearer " + token;
        return userRestClient.getUsers(auth);
    }

    public User getUser(String userId, String token) throws MalformedURLException {
        URL url = new URL(config.getKeycloakUrl() + "/admin/realms/" + config.getKeycloakRealm());
        UserApi userRestClient = RestClientBuilder.newBuilder().baseUrl(url).build(UserApi.class);
        String auth = "Bearer " + token;
        return userRestClient.getUser(auth, userId);
    }
}
