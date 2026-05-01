package com.Api_Gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

//    @Bean
//    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
//        http
//            .csrf(csrf -> csrf.disable())
//            .httpBasic(basic -> basic.disable())
//            .formLogin(form -> form.disable())
//            .authorizeExchange(auth -> auth
//                .anyExchange().permitAll()  // ✅ anyExchange() not anyRequest()
//            );
//        return http.build();
//    }
	
	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
	    http
	        .csrf(csrf -> csrf.disable())
	        .httpBasic(basic -> basic.disable())
	        .formLogin(form -> form.disable())
	        .headers(headers -> headers.disable())
	        // Disable Spring Security's own CORS filter — CORS is handled exclusively
	        // by the gateway's globalcors CorsWebFilter (application.yml). Leaving it
	        // enabled causes Spring Security to register a second CorsWebFilter that
	        // duplicates Access-Control-* headers on every response.
	        .cors(cors -> cors.disable())
	        .authorizeExchange(auth -> auth
	            .pathMatchers(
	                "/swagger-ui/**",
	                "/swagger-ui.html",
	                "/v3/api-docs/**",
	                "/webjars/**",
	                "/auth/**"
	            ).permitAll()
	            .anyExchange().permitAll()
	        );
	    return http.build();
	}
}