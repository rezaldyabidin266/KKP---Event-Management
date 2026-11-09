package com.skyline.skatecross.common.config;

import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;

@Configuration
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP,
        scheme = "bearer", bearerFormat = "JWT")
public class SwaggerJwtConfig {

    @Bean
    public OpenApiCustomiser jwtOpenApiCustomiser() {
        return openApi -> {
            if (openApi.getPaths() == null) {
                return;
            }
            openApi.getPaths().forEach((path, item) -> {
                boolean publicPath = path.equals("/api/auth/login")
                        || path.equals("/api/health")
                        || path.equals("/actuator/health");
                if (!publicPath) {
                    item.readOperations().forEach(operation ->
                            operation.addSecurityItem(
                                    new SecurityRequirement().addList("bearerAuth")));
                }
            });
        };
    }
}
