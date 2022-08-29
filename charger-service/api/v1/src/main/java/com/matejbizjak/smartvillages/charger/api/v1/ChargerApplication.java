package com.matejbizjak.smartvillages.charger.api.v1;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/v1")
@OpenAPIDefinition(
        info = @Info(title = "Charger service", version = "1.0.0", contact = @Contact(name = "Matej Bizjak")
                , description = "Service for managing chargers.", license = @License)
)
public class ChargerApplication extends Application {
}
