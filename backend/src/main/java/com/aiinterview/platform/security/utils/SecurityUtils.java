package com.aiinterview.platform.security.utils;

import jakarta.servlet.http.HttpServletRequest;

public class SecurityUtils {
    public static String getClientIP (HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // Trả về IP đầu tiên trong chuỗi vì đó là IP gốc của máy Client
           return xForwardedFor.split(",") [0].trim();
        }
        // Nếu không có proxy -> lấy trực tiếp
        return request.getRemoteAddr();
    }
}
