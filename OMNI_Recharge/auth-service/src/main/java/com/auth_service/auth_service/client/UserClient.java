
package com.auth_service.auth_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.auth_service.auth_service.dto.UserResponse;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {

    @GetMapping("/validate")
    UserResponse validateUser(
        @RequestParam("username") String username,
        @RequestParam("password") String password
    );
    @PostMapping("/register")
    UserResponse registerUser(
        @org.springframework.web.bind.annotation.RequestBody com.auth_service.auth_service.dto.AuthRequest request
    );

    //to check duplicate
    @GetMapping("/exists")
    boolean userExists(
        @RequestParam String username
    );
}