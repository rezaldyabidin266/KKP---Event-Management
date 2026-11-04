
package com.skyline.skatecross.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI skateCrossOpenAPI() {

        return new OpenAPI()
                .info(
                        new Info()
                                .title("Skate Cross Competition API")
                                .version("1.0.0")
                                .description(
                                        "REST API Event Management "
                                                + "Skate Cross Competition "
                                                + "pada Skyline Rollerskate Club. "
                                                + "Backend menggunakan Java 8, "
                                                + "Spring Boot, dan PostgreSQL.")
                                .contact(
                                        new Contact()
                                                .name("Skyline Rollerskate Club"))
                                .license(
                                        new License()
                                                .name("Internal Project")))
                .addServersItem(
                        new Server()
                                .url("http://localhost:8081")
                                .description("Local Development Server"));
    }
}
