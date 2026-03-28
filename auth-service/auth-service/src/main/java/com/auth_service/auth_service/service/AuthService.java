
package com.auth_service.auth_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth_service.auth_service.client.UserClient;
import com.auth_service.auth_service.dto.UserResponse;

@Service
public class AuthService {
	@Autowired
    private UserClient userClient;

    public UserResponse authenticate(String username, String password) {
        return userClient.validateUser(username, password);
    }

}