package com.aiinterview.platform.service.impl;

import com.aiinterview.platform.common.exception.BadRequestException;
import com.aiinterview.platform.model.dto.request.LoginRequest;
import com.aiinterview.platform.model.dto.request.RegisterRequest;
import com.aiinterview.platform.model.dto.response.AuthResponse;
import com.aiinterview.platform.model.entity.User;
import com.aiinterview.platform.model.repository.UserRepository;
import com.aiinterview.platform.security.JwtService;
import com.aiinterview.platform.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("")
    private String baseUrl;

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MINUTES = 15;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already in use");
        }

        String verificationToken = UUID.randomUUID().toString();

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .enabled(false)
                .verificationToken(verificationToken)
                .verificationTokenExpiry(LocalDateTime.now().plusHours(24))
                .build();

        userRepository.save(user);

        System.out.println("=== EMAIL VERIFICATION ===");
        System.out.println("To: " + user.getEmail());
        System.out.println("Verify link: " + baseUrl + "/api/v1/auth/verify?token=" + verificationToken);
        System.out.println("=== END ===");

        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Email khong ton tai"));

        if (!user.isEnabled()) {
            throw new BadRequestException("Tai khoan chua duoc kich hoat. Vui long kiem tra email.");
        }

        if (user.getLockUntil() != null && LocalDateTime.now().isBefore(user.getLockUntil())) {
            throw new BadRequestException("Tai khoan da bi khoa do nhap sai qua nhieu lan.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            user.setLoginAttempts(0);
            user.setLockUntil(null);
            userRepository.save(user);

        } catch (BadCredentialsException e) {
            user.setLoginAttempts(user.getLoginAttempts() + 1);

            if (user.getLoginAttempts() >= MAX_LOGIN_ATTEMPTS) {
                user.setLockUntil(LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));
                userRepository.save(user);
                throw new BadRequestException("Mat khau sai. Tai khoan da bi khoa 15 phut.");
            }

            userRepository.save(user);
            throw new BadRequestException("Mat khau sai");
        }

        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        String email = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new BadRequestException("Refresh token is expired or invalid");
        }
        return buildAuthResponse(user);
    }

    @Override
    public String verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new BadRequestException("Token xac thuc khong hop le"));

        if (LocalDateTime.now().isAfter(user.getVerificationTokenExpiry())) {
            throw new BadRequestException("Link xac thuc da het han (qua 24 gio)");
        }

        user.setEnabled(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);

        return "Tai khoan da duoc kich hoat thanh cong! Ban co the dang nhap ngay.";
    }

    @Override
    public void resendVerification(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Email khong ton tai trong he thong"));

        if (user.isEnabled()) {
            throw new BadRequestException("Tai khoan da duoc kich hoat");
        }

        String newToken = UUID.randomUUID().toString();
        user.setVerificationToken(newToken);
        user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));
        userRepository.save(user);

        System.out.println("=== RESEND VERIFICATION ===");
        System.out.println("To: " + user.getEmail());
        System.out.println("Verify link: " + baseUrl + "/api/v1/auth/verify?token=" + newToken);
        System.out.println("=== END ===");
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .user(AuthResponse.UserInfo.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .phone(user.getPhone())
                        .role(user.getRole().name())
                        .build())
                .build();
    }
}
