package com.user_service.user_service.dto;

public class UserResponse {

	private Long id;
	private String username;
    private String role;
    private String token;
    public UserResponse(Long id, String username, String role,String token) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.token = token;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public String getRole() {
		return role;
	}

	public UserResponse() {
		super();
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
