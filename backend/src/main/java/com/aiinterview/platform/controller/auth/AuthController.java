package com.aiinterview.platform.controller.auth;

import com.aiinterview.platform.common.response.ApiResponse;
import com.aiinterview.platform.model.dto.request.LoginRequest;
import com.aiinterview.platform.model.dto.request.RegisterRequest;
import com.aiinterview.platform.model.dto.response.AuthResponse;
import com.aiinterview.platform.model.dto.response.LoginResponse;
import com.aiinterview.platform.model.dto.response.RegisterResponse;
import com.aiinterview.platform.security.utils.JwtUtils;
import com.aiinterview.platform.service.LoginInterface;
import com.aiinterview.platform.service.PasswordResetService;
import com.aiinterview.platform.service.RegisterInterface;
import com.aiinterview.platform.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginInterface loginInterface;
    private final RegisterInterface registerInterface;
    private final PasswordResetService passwordResetService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request,
                                                               HttpServletRequest httpRequest) {
        RegisterResponse response = registerInterface.register(
                request.getEmail(), request.getPassword(),
                request.getFullName(),request.getPhone(),
                httpRequest
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response.getMessage(), null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request,
                                                             HttpServletRequest httpRequest) {
        LoginResponse response = loginInterface.login(
                request.getEmail(), request.getPassword(), httpRequest
        );
        // Load user info để trả về AuthResponse đầy đủ cho frontend
        var user = userService.findByEmail(request.getEmail());
        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(response.getAccessToken())
                .refreshToken(response.getRefreshToken())
                .tokenType("Bearer")
                .user(AuthResponse.UserInfo.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .phone(user.getPhone())
                        .role(user.getRole().name())
                        .build())
                .build();
        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", authResponse));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        LoginResponse lr = JwtUtils.refreshAccessToken(refreshToken);
        // Extract email từ refresh token để load user info
        String email = com.aiinterview.platform.security.jwt.JwtUtil
                .validateRefreshToken(refreshToken).getSubject();
        var user = userService.findByEmail(email);
        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(lr.getAccessToken())
                .refreshToken(lr.getRefreshToken())
                .tokenType("Bearer")
                .user(AuthResponse.UserInfo.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .phone(user.getPhone())
                        .role(user.getRole().name())
                        .build())
                .build();
        return ResponseEntity.ok(ApiResponse.success("Token refreshed", authResponse));
    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestParam("token") String token) {
        String message = registerInterface.verifyEmail(token);
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<Void>> resendVerification(@RequestBody Map<String, String> request) {
        registerInterface.resendVerification(request.get("email"));
        return ResponseEntity.ok(ApiResponse.success("Email xac thuc da duoc gui lai."));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestBody Map<String, String> request) {
        String message = passwordResetService.forgotPassword(request.get("email"));
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody Map<String, String> request) {
        String message = passwordResetService.resetPassword(
                request.get("token"), request.get("password")
        );
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    /** GET /api/v1/auth/me — trả thông tin user hiện tại dựa vào JWT */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponse.UserInfo>> getMe(Authentication authentication) {
        var user = userService.findByEmail(authentication.getName());
        AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .build();
        return ResponseEntity.ok(ApiResponse.success(userInfo));
    }
}
