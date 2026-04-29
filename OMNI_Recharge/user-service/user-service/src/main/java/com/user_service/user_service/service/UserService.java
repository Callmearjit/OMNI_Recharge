package com.user_service.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.user_service.user_service.dto.UserRequest;
import com.user_service.user_service.dto.UserResponse;
import com.user_service.user_service.entity.User;
import com.user_service.user_service.repository.UserRepository;

@Service
public class UserService {
	@Autowired
    private UserRepository userRepository;
	@Autowired
    private BCryptPasswordEncoder passwordEncoder;
	 public String register(UserRequest request) {

	        User user = new User();
	        user.setUsername(request.getUsername());
	        user.setEmail(request.getEmail());

	        user.setPassword(passwordEncoder.encode(request.getPassword()));

	        user.setRole("USER");

	        userRepository.save(user);

	        return "User registered successfully";
	    }
	 public UserResponse validateUser(String username, String password) {

	        User user = userRepository.findByUsername(username)
	                .orElseThrow(() -> new RuntimeException("User not found"));

	        if (passwordEncoder.matches(password, user.getPassword())) {
	            return new UserResponse(user.getUsername(), user.getRole());
	        }

	        throw new RuntimeException("Invalid credentials");
	    }

}
