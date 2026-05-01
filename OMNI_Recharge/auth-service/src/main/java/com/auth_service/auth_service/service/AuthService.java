package com.auth_service.auth_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth_service.auth_service.client.UserClient;
import com.auth_service.auth_service.dto.AuthRequest;
import com.auth_service.auth_service.dto.UserResponse;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserClient userClient;

    public UserResponse authenticate(String username, String password) {
        try {
            return userClient.validateUser(username, password);
        } catch (Exception e) {
            // ─────────────────────────────────────────────────────────────
            // FIX: Log the actual error instead of swallowing it silently.
            // Returning null here is intentional — AuthController maps null
            // to a 401 response, which is the correct HTTP semantics for
            // a failed login attempt.
            // ─────────────────────────────────────────────────────────────
            log.warn("Authentication failed for user '{}': {}", username, e.getMessage());
            return null;
        }
    }

    public boolean userExists(String username) {
        try {
            return userClient.userExists(username);
        } catch (Exception e) {
            // ─────────────────────────────────────────────────────────────
            // FIX: Log the error. Returning false on exception is safe —
            // registration will proceed and user-service will enforce its own
            // duplicate-username check at the DB level.
            // ─────────────────────────────────────────────────────────────
            log.error("Could not check existence for user '{}': {}", username, e.getMessage());
            return false;
        }
    }

    public UserResponse register(AuthRequest request) {
        try {
            return userClient.registerUser(request);
        } catch (feign.FeignException.Conflict e) {
            // ─────────────────────────────────────────────────────────────
            // FIX: Previously this catch block called e.printStackTrace()
            // and silently returned null. The caller (AuthController) then
            // returned a generic 500 "Registration failed" body, hiding the
            // real reason (username conflict, validation error, downstream
            // service down, etc.).
            //
            // Now we re-throw typed exceptions so the GlobalExceptionHandler
            // can map them to correct HTTP status codes.
            // ─────────────────────────────────────────────────────────────
            log.warn("Registration conflict for user '{}': {}", request.getUsername(), e.getMessage());
            throw new IllegalStateException("Username already exists in user-service");
        } catch (feign.FeignException e) {
            log.error("Feign call to user-service failed during register: status={} body={}",
                    e.status(), e.contentUTF8());
            throw new RuntimeException("User service is unavailable. Please try again later.");
        } catch (Exception e) {
            log.error("Unexpected error during registration for user '{}': {}",
                    request.getUsername(), e.getMessage(), e);
            throw new RuntimeException("Registration failed due to an internal error.");
        }
    }
}