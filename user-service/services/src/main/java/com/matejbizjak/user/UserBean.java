package com.matejbizjak.user;

import com.matejbizjak.user.config.ConfigProperties;
import com.matejbizjak.user.restclient.UserService;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@ApplicationScoped
public class UserBean {

    @Inject
    private ConfigProperties config;

    public List<User> getUsers(String token) throws MalformedURLException {
        URL url = new URL(config.getKeycloakUrl() + "/admin/realms/" + config.getKeycloakRealm());
        UserService userRestClient = RestClientBuilder.newBuilder().baseUrl(url).build(UserService.class);
        String auth = "Bearer " + token;
        return userRestClient.getUsers(auth);
    }

    public User getUser(String userId, String token) throws MalformedURLException {
        URL url = new URL(config.getKeycloakUrl() + "/admin/realms/" + config.getKeycloakRealm());
        UserService userRestClient = RestClientBuilder.newBuilder().baseUrl(url).build(UserService.class);
        String auth = "Bearer " + token;
        return userRestClient.getUser(auth, userId);
    }
}
