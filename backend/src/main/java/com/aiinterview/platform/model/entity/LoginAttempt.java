package com.aiinterview.platform.model.entity;

import java.time.LocalDateTime;

public class LoginAttempt {
    private String email;
    private String ipAddress;
    private boolean success;
    private LocalDateTime timestamp;

    public LoginAttempt(String email, String ipAddress, boolean success) {
        this.email = email;
        this.ipAddress = ipAddress;
        this.success = success;
        this.timestamp = LocalDateTime.now();
    }

    public String getEmail() {return email;}
    public String getIpAddress() {return ipAddress;}
    public boolean isSuccess() {return success;}
    public LocalDateTime getTimestamp() {return timestamp;}
}
