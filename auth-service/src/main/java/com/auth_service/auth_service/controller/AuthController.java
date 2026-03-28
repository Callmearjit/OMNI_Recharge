package com.auth_service.auth_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth_service.auth_service.dto.AuthRequest;
import com.auth_service.auth_service.dto.UserResponse;
import com.auth_service.auth_service.security.JwtUtil;
import com.auth_service.auth_service.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {

        UserResponse user = authService.authenticate(
                request.getUsername(),
                request.getPassword()
        );

        if (user == null) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        String token = jwtUtil.generateToken(
                user.getUsername(),
                user.getRole()
        );

        return ResponseEntity.ok(token);
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {

        // 1. Check if user already exists
        if (authService.userExists(request.getUsername())) {
            return ResponseEntity.status(409).body("Username already exists");
        }

        // 2. Register via user-service
        String responseContent = authService.register(request);

        // 3. Handle failure
        if (responseContent == null) {
            return ResponseEntity.status(500).body("Registration failed");
        }

        // 4. Return success
        return ResponseEntity.status(201).body("User registered successfully");
    }
}
