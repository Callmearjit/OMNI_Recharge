
package com.auth_service.auth_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.auth_service.auth_service.dto.UserResponse;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {

    @GetMapping("/users/validate")
    UserResponse validateUser(
        @RequestParam String username,
        @RequestParam String password
    );
}