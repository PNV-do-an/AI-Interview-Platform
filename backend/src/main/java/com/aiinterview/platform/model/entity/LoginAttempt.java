package com.aiinterview.platform.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "login_attempts", indexes = {
        @Index(name = "idx_login_email_timestamp", columnList = "email,created_at"),
        @Index(name = "idx_login_ip_timestamp", columnList = "ip_address,created_at")
})
public class LoginAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    protected LoginAttempt() {
    }

    private String email;
    private String ipAddress;
    private boolean success;
    // Bảng thật do Hibernate tạo từ bản entity cũ tên cột là created_at; entity đang dùng tên
    // timestamp nên insert thiếu cột NOT NULL này và fail.
    @Column(name = "created_at", nullable = false)
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
