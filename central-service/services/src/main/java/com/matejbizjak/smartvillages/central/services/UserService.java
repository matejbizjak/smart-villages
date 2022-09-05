package com.matejbizjak.smartvillages.central.services;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.matejbizjak.smartvillages.central.services.config.UrlProperties;
import com.matejbizjak.smartvillages.central.services.restclients.UserApi;
import com.matejbizjak.smartvillages.userlib.v1.User;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.URL;
import java.util.List;

@ApplicationScoped
public class UserService {

    @Inject
    private UrlProperties urlProperties;

    private final Logger LOG = LogManager.getLogger(UserService.class.getSimpleName());

    public List<User> getAllUsers() {
        try {
            UserApi userApi = RestClientBuilder.newBuilder()
                    .baseUrl(new URL(urlProperties.getUserApi())) // TODO service discovery, fault tolerance etc.
                    .build(UserApi.class);

            List<User> allUsers = userApi.getAllUsers();
            LOG.info("Received users via REST client.");
            return allUsers;
        } catch (Exception e) {
            LOG.error(e);
            throw new RuntimeException(e);
        }
    }

}
