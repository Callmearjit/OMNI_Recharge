package com.auth_service.auth_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.auth_service.auth_service.client.UserClient;
import com.auth_service.auth_service.dto.UserResponse;
import com.auth_service.auth_service.service.AuthService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserClient userClient;

    @InjectMocks
    private AuthService authService;

    //test for successful login
    @Test
    void testAuthenticate_Success() {
        UserResponse mockUser = new UserResponse();
        mockUser.setUsername("arjit");
        mockUser.setRole("ROLE_USER");

        when(userClient.validateUser("arjit", "1234")).thenReturn(mockUser);

        UserResponse result = authService.authenticate("arjit", "1234");

        assertNotNull(result);
        assertEquals("arjit", result.getUsername());
        assertEquals("ROLE_USER", result.getRole());
    }

    //Test for login fail (user-service throws exception)
    @Test
    void testAuthenticate_Failure() {
        when(userClient.validateUser("arjit", "wrong"))
                .thenThrow(new RuntimeException("Not found"));

        UserResponse result = authService.authenticate("arjit", "wrong");

        assertNull(result); 
    }

    // test for successful Registeration
    @Test
    void testRegister_Success() {
        com.auth_service.auth_service.dto.AuthRequest req = new com.auth_service.auth_service.dto.AuthRequest("newuser", "pass123", "test@test.com", "USER");

        when(userClient.registerUser(org.mockito.ArgumentMatchers.any())).thenReturn("Success");

        String result = authService.register(req);

        assertNotNull(result);
        assertEquals("Success", result);
    }

    //User exists check
    @Test
    void testUserExists_True() {
        when(userClient.userExists("arjit")).thenReturn(true);

        boolean exists = authService.userExists("arjit");

        assertTrue(exists);
    }

    //User does not exist
    @Test
    void testUserExists_False() {
        when(userClient.userExists("unknown")).thenReturn(false);

        boolean exists = authService.userExists("unknown");

        assertFalse(exists);
    }
}
