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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginInterface loginInterface;
    private final RegisterInterface registerInterface;
    private final PasswordResetService passwordResetService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request,
                                                               HttpServletRequest httpRequest) {
        RegisterResponse response = registerInterface.register(
                request.getEmail(), request.getPassword(),
                request.getFullName(), request.getPhone(),
                httpRequest
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response.getMessage(), null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request,
                                                             HttpServletRequest httpRequest) {
        LoginResponse response = loginInterface.login(
                request.getEmail(), request.getPassword(), httpRequest
        );
        return ResponseEntity.ok(ApiResponse.success("Dang nhap thanh cong", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        LoginResponse response = JwtUtils.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed", response));
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
}
