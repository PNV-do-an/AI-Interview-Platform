package com.hoc.backend.SpringBoot.dto;

public class LoginResponse {

    private String token;
//    private String account;

    public LoginResponse(String token) {
        this.token = token;
    }

//    public LoginResponse(String token,String account) {
//        this.token = token;
//        this.account = account;
//    }

    public String getToken() {
        return token;
    }

//    public String getAccount() {return  account;}

}
