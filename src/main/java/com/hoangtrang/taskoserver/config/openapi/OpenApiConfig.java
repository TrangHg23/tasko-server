package com.hoangtrang.taskoserver.config.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
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
                .info(new Info().title(title).version(version))
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "bearerAuth",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT"))
                                .addResponses("badRequest", createResponse("Bad Request"))
                                .addResponses("unauthorized", createResponse("Unauthorized"))
                                .addResponses("notFound", createResponse("Not Found"))
                                .addResponses("internalServerError", createResponse("Internal Server Error"))
                )
                .security(List.of(new SecurityRequirement().addList("bearerAuth")));
    }

    public GroupedOpenApi groupedOpenApi(@Value("${openapi.service.api-docs}") String group) {
        return GroupedOpenApi.builder()
                .group(group)
                .packagesToScan("com.hoangtrang.taskoserver.controller")
                .build();

    }

    private ApiResponse createResponse(String description) {
        return new ApiResponse()
                .description(description)
                .content(new Content().addMediaType("application/json",
                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"))));
    }
}
