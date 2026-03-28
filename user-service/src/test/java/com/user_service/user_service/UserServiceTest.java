package com.user_service.user_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.user_service.user_service.dto.UserRequest;
import com.user_service.user_service.dto.UserResponse;
import com.user_service.user_service.entity.User;
import com.user_service.user_service.enums.Role;
import com.user_service.user_service.repository.UserRepository;
import com.user_service.user_service.security.JwtUtil;
import com.user_service.user_service.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    //Register success
    @Test
    void testRegister_Success() {
        UserRequest request = new UserRequest();
        request.setUsername("arjit");
        request.setEmail("arjit@gmail.com");
        request.setPassword("pass123");

        when(userRepository.findByUsername("arjit")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass123")).thenReturn("hashed_pass");

        String result = userService.register(request);

        assertEquals("User registered successfully", result);
        verify(userRepository, times(1)).save(any());
    }

    //Register duplicate username
    @Test
    void testRegister_DuplicateUsername() {
        UserRequest request = new UserRequest();
        request.setUsername("arjit");
        request.setPassword("pass123");

        User existing = new User();
        existing.setUsername("arjit");

        when(userRepository.findByUsername("arjit")).thenReturn(Optional.of(existing));

        assertThrows(RuntimeException.class, () -> userService.register(request));
        verify(userRepository, never()).save(any());
    }

    //Login success
    @Test
    void testLogin_Success() {
        UserRequest request = new UserRequest();
        request.setUsername("arjit");
        request.setPassword("pass123");

        User user = new User();
        user.setUsername("arjit");
        user.setPassword("hashed_pass");
        user.setRole(Role.USER);

        when(userRepository.findByUsername("arjit")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass123", "hashed_pass")).thenReturn(true);
        when(jwtUtil.generateToken("arjit","USER")).thenReturn("mock_token");

        UserResponse response = userService.login(request);

        assertNotNull(response);
        assertEquals("arjit", response.getUsername());
        assertEquals("USER", response.getRole());
        assertEquals("mock_token", response.getToken());
    }

    //Login user not found
    @Test
    void testLogin_UserNotFound() {
        UserRequest request = new UserRequest();
        request.setUsername("unknown");
        request.setPassword("pass123");

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.login(request));
    }

    //Login wrong password
    @Test
    void testLogin_WrongPassword() {
        UserRequest request = new UserRequest();
        request.setUsername("arjit");
        request.setPassword("wrong");

        User user = new User();
        user.setUsername("arjit");
        user.setPassword("hashed_pass");
        user.setRole(Role.USER);

        when(userRepository.findByUsername("arjit")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed_pass")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> userService.login(request));
    }

    //userExists true
    @Test
    void testUserExists_True() {
        User user = new User();
        user.setUsername("arjit");

        when(userRepository.findByUsername("arjit")).thenReturn(Optional.of(user));

        assertTrue(userService.userExists("arjit"));
    }

    //userExists false
    @Test
    void testUserExists_False() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertFalse(userService.userExists("unknown"));
    }
}
