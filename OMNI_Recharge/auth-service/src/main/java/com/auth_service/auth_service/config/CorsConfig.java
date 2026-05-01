package com.auth_service.auth_service.config;

import org.springframework.context.annotation.Configuration;

// CORS is handled exclusively by the API Gateway (globalcors + DedupeResponseHeader).
// Adding a WebMvcConfigurer CORS bean here causes a duplicate
// Access-Control-Allow-Origin header (*, *) which browsers reject.
@Configuration
public class CorsConfig {
}
