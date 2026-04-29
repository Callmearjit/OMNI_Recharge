package com.user_service.user_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
	    "eureka.client.enabled=false",
	    "spring.cloud.config.enabled=false",
	    "spring.cloud.discovery.enabled=false"
	})
class UserServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
