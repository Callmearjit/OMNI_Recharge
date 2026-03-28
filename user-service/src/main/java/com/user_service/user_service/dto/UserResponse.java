package com.user_service.user_service.dto;

public class UserResponse {

	private String username;
    private String role;
    private String token;
    public UserResponse(String username, String role,String token) {
        this.username = username;
        this.role = role;
        this.token = token;
    }

	public String getUsername() {
		return username;
	}

	public String getRole() {
		return role;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
    
}
