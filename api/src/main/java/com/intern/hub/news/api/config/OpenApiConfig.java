package com.intern.hub.news.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Value("${services.gateway.url:http://localhost:8765}")
  private String gatewayUrl;

  @Bean
  public OpenApiCustomizer openApiCustomizer() {
    return openApi -> openApi
        .addServersItem(new Server().url(gatewayUrl + "/api"))
        .components(openApi.getComponents() == null ? new Components() : openApi.getComponents());
  }

}
