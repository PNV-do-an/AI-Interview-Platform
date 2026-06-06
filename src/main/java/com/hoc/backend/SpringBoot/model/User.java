package com.hoc.backend.SpringBoot.model;

import java.time.LocalDateTime;

public class User {

    private Long id;
    private String nameAccount;
    private String account;
    private String passWord;
    private String userName;
    private String role;
    private int counterFail = 0;
    private boolean locked;
    private LocalDateTime lockedUntil;
    private Boolean Active = false;

    public User () {}
    public User(String account, String passWord, String role) {
        this.account = account;
        this.passWord = passWord;
        this.role = role;
    }

    public User(String account, String passWord, String userName, String role) {
        this.account = account;
        this.passWord = passWord;
        this.userName = userName;
        this.role = role;
    }

    public void setId(Long id) {this.id = id;}

    public Long getId() {return id;}

    public String getUserName() {
        return userName;
    }

    public String getAccount() {
        return account;
    }

    public String getPassWord() {
        return passWord;
    }

    public String getRole() {
        return role;
    }

    public int getCounterFail() {
        return counterFail;
    }

    public void setCounterFail(int counterFail) {
        this.counterFail = counterFail;
    }

    public boolean getLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public LocalDateTime getLockedUntil() {
        return lockedUntil;
    }

    public void setLockedUntil(LocalDateTime lockedUntil) {
        this.lockedUntil = lockedUntil;
    }
}