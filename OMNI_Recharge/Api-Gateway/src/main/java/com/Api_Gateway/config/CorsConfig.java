package com.Api_Gateway.config;

import org.springframework.context.annotation.Configuration;

// CORS is configured via spring.cloud.gateway.globalcors in application.yml.
// A standalone CorsWebFilter bean conflicts with the gateway's response writing
// (ReadOnlyHttpHeaders.put UnsupportedOperationException) because the gateway
// commits response headers before the WebFilter can add CORS headers.
@Configuration
public class CorsConfig {
}
