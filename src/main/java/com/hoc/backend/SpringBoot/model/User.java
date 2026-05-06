package com.hoc.backend.SpringBoot.model;

public class User {
    private String nameAccount;
    private String account; // có thể là số điện thoại hoặc email
    private String passWord;
    private String userName;
    private String role;

    public User(String account,String passWord, String role) {
        this.account = account;
        this.passWord = passWord;
        this.role = role;
    }

    public User(String account,String passWord,String userName,String role) {
        this.account = account;
        this.passWord = passWord;
        this.userName = userName;
        this.role = role;
    }

    public String getUserName() {
        return  userName;
    };

    public String getAccount() {
        return account;
    }

    public String getPassWord() {

        return passWord;
    }

    public String getRole() {
        return role;
    };
}
