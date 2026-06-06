package com.hoc.backend.SpringBoot.dto;

import java.time.LocalDateTime;

public class LoginResponse {

    private final String accessToken;
    private final String refreshToken;

    public LoginResponse(String accessToken,String refreshToken)
    {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

//    public LoginResponse(String token,String account) {
//        this.token = token;
//        this.account = account;
//    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

//    public String getAccount() {return  account;}

}
