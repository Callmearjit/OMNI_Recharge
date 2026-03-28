package com.Api_Gateway.security;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
public class JwtFilter implements GlobalFilter {

    @Autowired
    private JwtUtil jwtUtil;

    // ── Public endpoints (no token required) ─────────────────────────
    private final List<String> WHITELIST = Arrays.asList(
            "/auth/",
            "/users/register",
            "/users/login",
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-ui.html",
            "/webjars",
            "/swagger-resources"
    );

    // ── Admin-only paths (require ADMIN role) ────────────────────────
    // These are checked AFTER token validation
    private final List<String> ADMIN_ONLY_PATHS = Arrays.asList(
            "/recharges/all",
            "/payments/all",
            "/users/all"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        HttpMethod method = exchange.getRequest().getMethod();

        // ── 1. Allow whitelisted paths without token ─────────────────
        boolean isWhitelisted = WHITELIST.stream().anyMatch(path::contains);
        if (isWhitelisted) {
            return chain.filter(exchange);
        }

        // ── 2. Validate JWT token ────────────────────────────────────
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorizedResponse(exchange, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            return unauthorizedResponse(exchange, "Invalid or expired token");
        }

        Claims claims = jwtUtil.extractClaims(token);
        String username = claims.getSubject();
        String role = claims.get("role", String.class);

        if (role == null) {
            return unauthorizedResponse(exchange, "Token missing role claim");
        }

        // ── 3. Role-based route authorization ────────────────────────

        // Admin-only path check
        boolean isAdminOnlyPath = ADMIN_ONLY_PATHS.stream().anyMatch(path::startsWith);
        if (isAdminOnlyPath && !"ADMIN".equals(role)) {
            return forbiddenResponse(exchange, "Access Denied: Admin only");
        }

        // Operator write operations (POST/PUT/DELETE on /operators/plans) → ADMIN only
        if (path.contains("/operators/plans") && method != null &&
                (method.equals(HttpMethod.POST) || method.equals(HttpMethod.PUT) || method.equals(HttpMethod.DELETE))) {
            if (!"ADMIN".equals(role)) {
                return forbiddenResponse(exchange, "Access Denied: Only admins can manage plans");
            }
        }

        // ── 4. Forward authenticated request with user info headers ──
        ServerHttpRequest request = exchange.getRequest().mutate()
                .header("X-User-Id", username)
                .header("X-Role", role)
                .build();

        return chain.filter(exchange.mutate().request(request).build());
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");
        String body = "{\"error\": \"" + message + "\"}";
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes());
        return response.writeWith(Mono.just(buffer));
    }

    private Mono<Void> forbiddenResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().add("Content-Type", "application/json");
        String body = "{\"error\": \"" + message + "\"}";
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes());
        return response.writeWith(Mono.just(buffer));
    }
}