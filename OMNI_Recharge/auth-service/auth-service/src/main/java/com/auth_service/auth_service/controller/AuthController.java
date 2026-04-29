package com.auth_service.auth_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth_service.auth_service.dto.AuthRequest;
import com.auth_service.auth_service.dto.UserResponse;
import com.auth_service.auth_service.service.AuthService;
import com.auth_service.auth_service.util.JwtUtil;


@RestController
@RequestMapping("/auth")
public class AuthController {
	 @Autowired
	    private AuthService authService;

	 @Autowired
	 private JwtUtil jwtUtil;

	    @PostMapping("/login")
	    public String login(@RequestBody AuthRequest request) {

	        UserResponse user = authService.authenticate(
	                request.getUsername(),
	                request.getPassword()
	        );

	        if (user != null) {
	            return jwtUtil.generateToken(user.getUsername(), user.getRole());
	        } else {
	            throw new RuntimeException("Invalid credentials");
	        }
	    }

}
