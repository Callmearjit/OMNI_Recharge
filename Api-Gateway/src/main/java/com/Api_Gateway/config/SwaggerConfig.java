package com.Api_Gateway.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

//    @Bean
//    public OpenAPI gatewayOpenAPI() {
//        return new OpenAPI()
//                .info(new Info()
//                        .title("API Gateway")
//                        .description("Routes requests to microservices with JWT auth")
//                        .version("1.0"));
//    }
	
	@Bean
    public OpenAPI gatewayOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Gateway - Aggregated Docs")
                        .description("Routes requests to microservices with JWT auth")
                        .version("1.0"));
    }
}
