package com.recharge_service.recharge_service.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return (RequestTemplate template) -> {
            // ✅ Forward JWT token from incoming request to outgoing Feign calls
            ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null) {
                    template.header("Authorization", authHeader);
                }

                // ✅ Also forward X-User-Id and X-Role headers
                String userId = request.getHeader("X-User-Id");
                String role = request.getHeader("X-Role");
                if (userId != null) template.header("X-User-Id", userId);
                if (role != null) template.header("X-Role", role);
            }
        };
    }
}