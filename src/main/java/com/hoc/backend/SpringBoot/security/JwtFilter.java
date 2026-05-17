package com.hoc.backend.SpringBoot.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        System.out.println( ">>> AUTH HEADER " + authHeader);

        // Chỉ xử lý và log khi thấy có Header Authorization đúng định dạng Bearer
        if (authHeader != null && authHeader.toLowerCase().startsWith("bearer ")) {
            String token = authHeader.substring(7).trim();
            
            // Loại bỏ dấu ngoặc kép nếu có (format lúc gửi từ client có "")
            if (token.startsWith("\"") && token.endsWith("\"")) {
                token = token.substring(1, token.length() - 1);
            }
            
            String path = request.getRequestURI();
            
            System.out.println(">>> DEBUG: Final Token before validate: " + token );

            try {
                var claims = JwtUtil.validateAccessToken(token);
                String account = claims.getSubject();
                String role = (String) claims.get("role");

                var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
                var auth = new UsernamePasswordAuthenticationToken(account, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(auth);
                System.out.println(">>> Success: Authenticated user [" + account + "] for path [" + path + "]");

            } catch (Exception e) {
                System.err.println(">>> Error: JWT Validation Failed - " + e.getMessage());
                // Không set Authentication, Spring Security sẽ tự chặn ở các bước sau nếu cần
            }
        }

        // Cho phép request đi tiếp trong Filter Chain
        filterChain.doFilter(request, response);
    }
}