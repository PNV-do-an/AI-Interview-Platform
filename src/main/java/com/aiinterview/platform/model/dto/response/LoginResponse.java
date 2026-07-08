package com.hoc.backend.SpringBoot.dto;

public class LoginResponse {

    private final String accessToken;
    private final String refreshToken;
    private final String role;

    public LoginResponse(String accessToken, String refreshToken, String role) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.role = role;
    }

    public String getAccessToken() {return accessToken;}
    public String getRefreshToken() {return refreshToken;}
    public String getRole() {return role;}
}
