package com.aiinterview.platform.service;

public interface EmailService {
    void sendWelcomeEmail(String to, String username);
    void sendVerificationEmail(String to, String token);
    void sendVerificationEmail(String to, String token, String fullName);
    void sendPasswordResetEmail(String to, String token);
    void sendResetPasswordEmail(String to, String token, String fullName);
    void sendNotificationEmail(String to, String message);
    void sendAccountLockedEmail(String to, String fullName);
    void sendAccountDeletedEmail(String to, String fullName);
    void sendPasswordResetConfirmation(String to, String fullName);
    void sendResendVerificationEmail(String to, String token, String fullName);
}