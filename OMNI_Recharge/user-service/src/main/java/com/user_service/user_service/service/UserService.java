package com.user_service.user_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.user_service.user_service.dto.UserRequest;
import com.user_service.user_service.dto.UserResponse;
import com.user_service.user_service.entity.User;
import com.user_service.user_service.enums.Role;
import com.user_service.user_service.repository.UserRepository;
import com.user_service.user_service.security.JwtUtil;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String register(UserRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        if (request.getRole() != null && request.getRole().equalsIgnoreCase("ADMIN")) {
            user.setRole(Role.ADMIN);
        } else {
            user.setRole(Role.USER);
        }

        userRepository.save(user);
        log.info("User '{}' registered with role {} and ID {}", user.getUsername(), user.getRole(), user.getId());
        return "User registered successfully with ID: " + user.getId();
    }

    public UserResponse login(UserRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new UserResponse(user.getId(), user.getUsername(), user.getRole().name(), token);
    }

    public boolean userExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    // ✅ Added: for /profile endpoint
    public UserResponse getProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserResponse(user.getId(), user.getUsername(), user.getRole().name(), null);
    }

    // ✅ Added: for /all endpoint
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> new UserResponse(u.getId(), u.getUsername(), u.getRole().name(), null))
                .collect(Collectors.toList());
    }
}