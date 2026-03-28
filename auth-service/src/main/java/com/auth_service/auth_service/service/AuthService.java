
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
	        try {
	            return userClient.validateUser(username, password);
	        } catch (Exception e) {
	            return null;
	        }
	    }
	 public boolean userExists(String username) {
	        try {
	            return userClient.userExists(username);
	        } catch (Exception e) {
	            return false;
	        }
	    }

	   
	    public String register(com.auth_service.auth_service.dto.AuthRequest request) {
	        try {
	            return userClient.registerUser(request);
	        } catch (Exception e) {
                 e.printStackTrace();
	            return null;
	        }
	    }

}