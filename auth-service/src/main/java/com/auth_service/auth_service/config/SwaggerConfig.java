package com.auth_service.auth_service.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI authServiceOpenAPI() {
        return new OpenAPI().info(new Info().title("Auth Service API")
                .description("Handles login and registration")
                .version("1.0"));
    }
//    @Bean
//    public GroupedOpenApi publicApi() {
//        return GroupedOpenApi.builder()
//                .group("auth-service")
//                .packagesToScan("com.auth_service.auth_service.controller")
//                .build();
//    }
}