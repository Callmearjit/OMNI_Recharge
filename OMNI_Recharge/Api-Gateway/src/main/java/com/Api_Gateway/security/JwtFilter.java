package com.Api_Gateway.security;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
@Order(-1)
public class JwtFilter implements GlobalFilter {

    @Autowired
    private JwtUtil jwtUtil;

    private final List<String> WHITELIST = Arrays.asList(
            "/auth/login",
            "/auth/register",
            "/login",
            "/register",
            "/users/register",
            "/users/login",
            "/users/validate",
            "/users/exists",
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-ui.html",
            "/webjars",
            "/swagger-resources",
            "/auth/v3/api-docs",
            "/users/v3/api-docs",
            "/operators/v3/api-docs",
            "/payments/v3/api-docs",
            "/recharges/v3/api-docs",
            "/notifications/v3/api-docs"
    );

    private final List<String> PUBLIC_GET_PREFIXES = Arrays.asList(
            "/operators"
    );

    private final List<String> ADMIN_ONLY_PATHS = Arrays.asList(
            "/recharges/recharges/all",
            "/recharges/all",
            "/payments/payments/all",
            "/payments/all",
            "/users/all"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        HttpMethod method = exchange.getRequest().getMethod();

        System.out.println("JwtFilter path: " + path);

        // Allow OPTIONS (CORS)
        if (HttpMethod.OPTIONS.equals(method)) {
            return chain.filter(exchange);
        }

        boolean isWhitelisted = WHITELIST.stream().anyMatch(path::startsWith);

        if (!isWhitelisted && HttpMethod.GET.equals(method)) {
            isWhitelisted = PUBLIC_GET_PREFIXES.stream().anyMatch(path::startsWith);
        }

        if (isWhitelisted) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        // ❌ No header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorizedResponse(exchange, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        // ❌ Empty token
        if (token == null || token.trim().isEmpty()) {
            return unauthorizedResponse(exchange, "Empty JWT token");
        }

        // ❌ Invalid token
        if (!jwtUtil.validateToken(token)) {
            return unauthorizedResponse(exchange, "Invalid or expired token");
        }

        // ✅ SAFE parsing (FIX)
        Claims claims;
        try {
            claims = jwtUtil.extractClaims(token);
        } catch (Exception e) {
            return unauthorizedResponse(exchange, "Invalid JWT token format");
        }

        String username = claims.getSubject();
        String role = claims.get("role", String.class);

        if (role == null) {
            return unauthorizedResponse(exchange, "Token missing role claim");
        }

        boolean isAdminOnlyPath = ADMIN_ONLY_PATHS.stream().anyMatch(path::startsWith);
        if (isAdminOnlyPath && !"ADMIN".equals(role)) {
            return forbiddenResponse(exchange, "Access Denied: Admin only");
        }

        boolean isOperatorWrite = path.startsWith("/operators") && method != null &&
                (method.equals(HttpMethod.POST) || method.equals(HttpMethod.PUT)
                        || method.equals(HttpMethod.DELETE));

        if (isOperatorWrite && !"ADMIN".equals(role)) {
            return forbiddenResponse(exchange, "Access Denied: Only admins can manage operators");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.addAll(exchange.getRequest().getHeaders());
        headers.set("X-User-Id", username);
        headers.set("X-Role", role);

        ServerHttpRequest request = new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public HttpHeaders getHeaders() {
                return HttpHeaders.readOnlyHttpHeaders(headers);
            }
        };

        return chain.filter(exchange.mutate().request(request).build());
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"error\": \"" + message + "\"}";
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes());
        return response.writeWith(Mono.just(buffer));
    }

    private Mono<Void> forbiddenResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"error\": \"" + message + "\"}";
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes());
        return response.writeWith(Mono.just(buffer));
    }
}