package com.user_service.user_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.user_service.user_service.dto.UserRequest;
import com.user_service.user_service.dto.UserResponse;
import com.user_service.user_service.service.UserService;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserService userService; // ✅ Fixed: only service, no direct repository

    // ── Register ─────────────────────────────────────────────────────
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRequest request) {
        String response = userService.register(request);
        return ResponseEntity.status(201).body(response);
    }

    // ── Login ────────────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    // ── Validate (called by Auth Service via Feign) ──────────────────
    @GetMapping("/validate")
    public ResponseEntity<UserResponse> validate(@RequestParam String username,
                                                  @RequestParam String password) {
        UserRequest req = new UserRequest();
        req.setUsername(username);
        req.setPassword(password);
        return ResponseEntity.ok(userService.login(req));
    }

    // ── Check if user exists (called by Auth Service via Feign) ──────
    @GetMapping("/exists")
    public boolean exists(@RequestParam String username) {
        return userService.userExists(username);
    }

    // ── Get logged-in user's profile ─────────────────────────────────
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(
            @RequestHeader("X-User-Id") String username) {
        // ✅ Fixed: moved logic to service
        return ResponseEntity.ok(userService.getProfile(username));
    }

    // ── ADMIN: Get all users ─────────────────────────────────────────
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(
            @RequestHeader("X-Role") String role) {
        // ✅ Fixed: moved logic to service
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("Access Denied: Admin only");
        }
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}