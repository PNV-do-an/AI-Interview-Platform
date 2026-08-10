package com.aiinterview.platform.service;

public interface PasswordResetService {

    String forgotPassword(String email);

    String resetPassword(String token, String newPassword);
}
