
package com.auth_service.auth_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.auth_service.auth_service.dto.UserResponse;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {

    @GetMapping("/users/validate")
    UserResponse validateUser(
        @RequestParam("username") String username,
        @RequestParam("password") String password
    );
    @PostMapping("/users/register")
    String registerUser(
        @org.springframework.web.bind.annotation.RequestBody com.auth_service.auth_service.dto.AuthRequest request
    );

    //to check duplicate
    @GetMapping("/users/exists")
    boolean userExists(
        @RequestParam String username
    );
}