package com.aiinterview.platform.security.utils;

import com.aiinterview.platform.model.dto.response.LoginResponse;
import com.aiinterview.platform.security.jwt.JwtUtil;
import io.jsonwebtoken.Claims;

public class JwtUtils {

    public static LoginResponse refreshAccessToken(String refreshToken) {
        Claims claims = JwtUtil.validateRefreshToken(refreshToken);

        String email = claims.getSubject();
        String role  = (String) claims.get("role");

        String newAccessToken = JwtUtil.generateAccessToken(email, role);

        return new LoginResponse(newAccessToken, refreshToken, role);
    }
}
