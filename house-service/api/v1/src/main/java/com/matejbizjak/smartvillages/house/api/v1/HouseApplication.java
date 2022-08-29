package com.matejbizjak.smartvillages.house.api.v1;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/v1")
@OpenAPIDefinition(
        info = @Info(title = "House service", version = "1.0.0", contact = @Contact(name = "Matej Bizjak")
                , description = "Service for managing houses.", license = @License)
)
public class HouseApplication extends Application {
}
