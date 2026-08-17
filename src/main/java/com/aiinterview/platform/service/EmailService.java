package com.aiinterview.platform.service;

public interface EmailService {
    void sendAccountLockedEmail(String toEmail, String fullName);
    void sendAccountDeletedEmail(String toEmail, String fullName);
    void sendPasswordResetEmail(String toEmail, String fullName, String resetToken);
}
