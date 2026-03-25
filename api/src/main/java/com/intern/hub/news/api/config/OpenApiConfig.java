package com.intern.hub.news.api.config;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

        @Value("${services.gateway.url:http://localhost:8765}")
        private String gatewayUrl;

        @Bean
        public OpenApiCustomizer openApiCustomizer() {
                SecurityScheme bearerAuthScheme = new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT");

                SecurityScheme internalApiKeyScheme = new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-Internal-Secret");

                return openApi -> openApi
                                .addServersItem(new Server().url(gatewayUrl + "/api"))
                                .components((openApi.getComponents() == null ? new Components()
                                                : openApi.getComponents())
                                                .addSecuritySchemes("Bearer", bearerAuthScheme)
                                                .addSecuritySchemes("InternalAPIKey", internalApiKeyScheme));
        }
}