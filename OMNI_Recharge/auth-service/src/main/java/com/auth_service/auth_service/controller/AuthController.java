package com.auth_service.auth_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth_service.auth_service.dto.AuthRequest;
import com.auth_service.auth_service.dto.ErrorDTO;
import com.auth_service.auth_service.dto.UserResponse;
import com.auth_service.auth_service.security.JwtUtil;
import com.auth_service.auth_service.service.AuthService;

@RestController
@RequestMapping("/")
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorDTO(HttpStatus.UNAUTHORIZED.value(), "Invalid credentials"));
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return ResponseEntity.ok(java.util.Map.of("token", token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {

        if (authService.userExists(request.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorDTO(HttpStatus.CONFLICT.value(), "Username already exists"));
        }

        UserResponse registered = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(java.util.Map.of("token", registered.getToken()));
    }
}
