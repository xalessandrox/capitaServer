package com.sandro.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 16.09.2023
 */

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Alessandro Formica",
                        email = "formicale@hotmail.com",
                        url = ""
                ),
                description = "",
                title = "SecureCapita API Specification",
                version = "1.0",
                license = @License(name = "Licensed by: Sandro Enterprise Ltd."),
                termsOfService = "Terms of service"
        ),
        servers = {
                @Server(
                        description = "Develop environment",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "Test environment",
                        url = "Not active"
                )
        }
//        security = {@SecurityRequirement(name = "bearerAuth")}
)

//@SecurityScheme(
//        name = "bearerAuth",
//        description = "description of security scheme: bearer Auth",
//        scheme = "bearer",
//        type = SecuritySchemeType.HTTP,
//        bearerFormat = "JWT",
//        in = SecuritySchemeIn.HEADER
//)
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customizeOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new io.swagger.v3.oas.models.security.SecurityScheme()
                                .name(securitySchemeName)
                                .description("Insert a valid token to unlock the service")
                                .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                                .in(io.swagger.v3.oas.models.security.SecurityScheme.In.HEADER)
                                .scheme("bearer")
                                .bearerFormat("JWT")));


    }
}
