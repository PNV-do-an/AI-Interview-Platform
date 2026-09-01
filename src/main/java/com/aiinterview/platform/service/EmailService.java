package com.aiinterview.platform.service;

public interface EmailService {

    void sendVerificationEmail(String to, String fullName, String token);

    void sendResendVerificationEmail(String to, String fullName, String token);

    void sendResetPasswordEmail(String to, String fullName, String token);

    void sendPasswordResetConfirmation(String to, String fullName);

    void sendAccountLockedEmail(String to, String fullName);

    void sendAccountUnlockedEmail(String to, String fullName);

    void sendAccountDeletedEmail(String to, String fullName);
}
