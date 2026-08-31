package com.aiinterview.platform.service.impl;

import com.aiinterview.platform.common.exception.InvalidAccountLockedException;
import com.aiinterview.platform.common.exception.InvalidAccountException;
import com.aiinterview.platform.common.exception.InvalidPasswordException;
import com.aiinterview.platform.model.dto.response.LoginResponse;
import com.aiinterview.platform.model.entity.LoginAttempt;
import com.aiinterview.platform.model.entity.User;
import com.aiinterview.platform.model.repository.LoginAttemptRepository;
import com.aiinterview.platform.model.repository.UserRepository;
import com.aiinterview.platform.security.jwt.JwtUtil;
import com.aiinterview.platform.security.utils.SecurityUtils;
import com.aiinterview.platform.service.LoginInterface;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoginInterfaceImpl implements LoginInterface {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptRepository loginAttemptRepository;

    @Override
    public LoginResponse login(String email, String password, HttpServletRequest request) {
        String ipClient = SecurityUtils.getClientIP(request);

        // Bước 1: Check IP rate-limit — 20 lần thất bại từ IP này trong 15 phút (bất kể email nào)
        long ipFailures = loginAttemptRepository.countFailedByIp(ipClient, LocalDateTime.now().minusMinutes(15));
        if (ipFailures >= 20) {
            throw new InvalidAccountException(
                    "Hệ thống phát hiện hoạt động bất thường từ thiết bị của bạn. Vui lòng thử lại sau."
            );
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    loginAttemptRepository.addAttempt(new LoginAttempt(email, ipClient, false));
                    return new InvalidAccountException("Email không tồn tại");
                });

        if (!user.isEnabled()) {
            throw new InvalidAccountException("Tài khoản chưa được kích hoạt. Vui lòng kiểm tra email.");
        }

        // Kiểm tra admin lock (khóa bởi Admin, khác với lockUntil do nhập sai password)
        if (user.isLocked()) {
            throw new InvalidAccountLockedException(
                    "Tài khoản của bạn đã bị khóa bởi quản trị viên. Vui lòng liên hệ hỗ trợ.",
                    null
            );
        }

        if (user.getLockUntil() != null && LocalDateTime.now().isBefore(user.getLockUntil())) {
            throw new InvalidAccountLockedException(
                    "Tài khoản đã bị khóa do nhập sai quá nhiều lần. Vui lòng thử lại sau 15 phút.",
                    user.getLockUntil()
            );
        }

        long recentFailures = loginAttemptRepository.getFailedAttempts(
                email, LocalDateTime.now().minusMinutes(15)
        ).size();
        if (recentFailures >= 5) {
            user.setLockUntil(LocalDateTime.now().plusMinutes(15));
            userRepository.save(user);
            throw new InvalidAccountLockedException(
                    "Tài khoản đã bị khóa do nhập sai quá nhiều lần. Vui lòng thử lại sau 15 phút.",
                    user.getLockUntil()
            );
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            user.setLoginAttempts(user.getLoginAttempts() + 1);
            loginAttemptRepository.addAttempt(new LoginAttempt(email, ipClient, false));

            if (user.getLoginAttempts() >= 5) {
                user.setLockUntil(LocalDateTime.now().plusMinutes(15));
                userRepository.save(user);
                throw new InvalidAccountLockedException(
                        "Tài khoản đã bị khóa do nhập sai quá nhiều lần. Khóa đến: ",
                        user.getLockUntil()
                );
            }

            throw new InvalidPasswordException("Mật khẩu sai");
        }

        loginAttemptRepository.addAttempt(new LoginAttempt(email, ipClient, true));
        user.setLoginAttempts(0);
        user.setLockUntil(null);
        userRepository.save(user);

        String accessToken = JwtUtil.generateAccessToken(user.getEmail(), user.getRole().name());
        String refreshToken = JwtUtil.generateRefreshToken(user.getEmail(), user.getRole().name());

        return new LoginResponse(accessToken, refreshToken, user.getRole().name());
    }
}
