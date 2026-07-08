package com.aiinterview.platform.service;

import com.aiinterview.platform.model.dto.request.LoginRequest;
import com.aiinterview.platform.model.dto.request.RegisterRequest;
import com.aiinterview.platform.model.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String refreshToken);
    String verifyEmail(String token);
    void resendVerification(String email);
}
