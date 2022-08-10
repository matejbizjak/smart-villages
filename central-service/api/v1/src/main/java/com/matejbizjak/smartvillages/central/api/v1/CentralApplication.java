package com.matejbizjak.smartvillages.central.api.v1;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("v1")
@OpenAPIDefinition(
        info = @Info(title = "Central service", version = "1.0.0", contact = @Contact(name = "Matej Bizjak")
                , description = "Central service for coordinating and managing.", license = @License)
)
public class CentralApplication extends Application {
}
