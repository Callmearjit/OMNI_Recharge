package com.user_service.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.user_service.user_service.dto.UserRequest;
import com.user_service.user_service.dto.UserResponse;
import com.user_service.user_service.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
	@Autowired
    private UserService userService;

	@PostMapping("/register")
    public String register(@RequestBody UserRequest request) {
        return userService.register(request);
    }
	 @GetMapping("/validate")
	    public UserResponse validate(
	            @RequestParam String username,
	            @RequestParam String password) {

	        return userService.validateUser(username, password);
	    }
}
