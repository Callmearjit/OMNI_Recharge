package com.user_service.user_service.controller;

import java.util.List;
import java.util.stream.Collectors;

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
import com.user_service.user_service.entity.User;
import com.user_service.user_service.repository.UserRepository;
import com.user_service.user_service.service.UserService;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // ── Register ─────────────────────────────────────────────────────
    @PostMapping("/register")
    public String register(@RequestBody UserRequest request) {
        return userService.register(request);
    }

    // ── Login ────────────────────────────────────────────────────────
    @PostMapping("/login")
    public UserResponse login(@RequestBody UserRequest request) {
        return userService.login(request);
    }

    // ── Validate (used by auth-service via Feign) ────────────────────
    @GetMapping("/validate")
    public UserResponse validate(@RequestParam String username,
                                  @RequestParam String password) {
        UserRequest req = new UserRequest();
        req.setUsername(username);
        req.setPassword(password);
        return userService.login(req);
    }

    // ── Check if user exists (used by auth-service) ──────────────────
    @GetMapping("/exists")
    public boolean exists(@RequestParam String username) {
        return userService.userExists(username);
    }

    // ── Get logged-in user's profile ─────────────────────────────────
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(
            @RequestHeader("X-User-Id") String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserResponse response = new UserResponse(
                user.getUsername(),
                user.getRole().name(),
                null // no token needed for profile view
        );
        response.setToken(null);
        return ResponseEntity.ok(response);
    }

    // ── ADMIN: Get all users ─────────────────────────────────────────
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(
            @RequestHeader("X-Role") String role) {

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("Access Denied: Admin only");
        }

        List<UserResponse> users = userRepository.findAll().stream()
                .map(u -> new UserResponse(u.getUsername(), u.getRole().name(), null))
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }
}
