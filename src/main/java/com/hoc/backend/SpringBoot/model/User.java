package com.hoc.backend.SpringBoot.model;

import java.time.LocalDateTime;

public class User {

    private Long id = 0L;
    private String email;
    private String passWord;
    private String fullName;
    private String phone;
    private String role;
    private int counterFail = 0;
    private boolean locked;
    private LocalDateTime lockedUntil;
    private Boolean active = false;
    private String verificationToken;
    private LocalDateTime verificationTokenExpiry;

    public User() {}

    public User(Long id, String email, String passWord, String role) {
        this.id = id;
        this.email = email;
        this.passWord = passWord;
        this.role = role;
    }

    public User(String email, String passWord, String fullName, String phone, String role) {
        this.email = email;
        this.passWord = passWord;
        this.fullName = fullName;
        this.phone = phone;
        this.role = role;
    }

    public void setId(Long id) {this.id = id;}
    public Long getId() {return id;}
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
    public String getPassWord() {return passWord;}
    public void setPassWord(String passWord) {this.passWord = passWord;}
    public String getFullName() {return fullName;}
    public void setFullName(String fullName) {this.fullName = fullName;}
    public String getPhone() {return phone;}
    public void setPhone(String phone) {this.phone = phone;}
    public String getRole() {return role;}
    public void setRole(String role) {this.role = role;}
    public int getCounterFail() {return counterFail;}
    public void setCounterFail(int counterFail) {this.counterFail = counterFail;}
    public boolean getLocked() {return locked;}
    public void setLocked(boolean locked) {this.locked = locked;}
    public LocalDateTime getLockedUntil() {return lockedUntil;}
    public void setLockedUntil(LocalDateTime lockedUntil) {this.lockedUntil = lockedUntil;}
    public Boolean getActive() {return active;}
    public void setActive(Boolean active) {this.active = active;}
    public String getVerificationToken() {return verificationToken;}
    public void setVerificationToken(String verificationToken) {this.verificationToken = verificationToken;}
    public LocalDateTime getVerificationTokenExpiry() {return verificationTokenExpiry;}
    public void setVerificationTokenExpiry(LocalDateTime verificationTokenExpiry) {this.verificationTokenExpiry = verificationTokenExpiry;}
}
