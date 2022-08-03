package com.matejbizjak.user;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/")
@OpenAPIDefinition(
        info = @Info(title = "User service", version = "1.0.0", contact = @Contact(name = "Matej Bizjak")
                , description = "Service for managing users.")
)
public class UserApplication extends Application {
}
