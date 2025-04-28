package com.hoangtrang.taskoserver.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@Profile({"dev", "test"})
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPI(@Value("${openapi.service.title}") String title,
                           @Value("${openapi.service.version}") String version,
                           @Value("${openapi.service.server}") String server
    ) {
        return new OpenAPI()
                .servers(List.of(new Server().url(server)))
                .info(new Info().title(title).version(version));
    }

    public GroupedOpenApi groupedOpenApi(@Value("${openapi.service.api-docs}") String group) {
        return GroupedOpenApi.builder()
                .group(group)
                .packagesToScan("com.hoangtrang.taskoserver.controller")
                .build();

    }
}
