package com.hoc.backend.SpringBoot.service.InterviewPlatform;

import com.hoc.backend.SpringBoot.dto.LoginResponse;
import com.hoc.backend.SpringBoot.exception.InvaildAccountLockedException;
import com.hoc.backend.SpringBoot.exception.InvalidAccountException;
import com.hoc.backend.SpringBoot.exception.InvalidPassWordException;
import com.hoc.backend.SpringBoot.model.LoginAttempt;
import com.hoc.backend.SpringBoot.model.User;
import com.hoc.backend.SpringBoot.repository.LoginAttemptRepository;
import com.hoc.backend.SpringBoot.repository.UserRepository;
import com.hoc.backend.SpringBoot.security.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.hoc.backend.SpringBoot.security.utils.SecurityUtils.getClientIP;

@Service
public class LoginService  {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptRepository loginAttemptRepository;

    public LoginService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                        LoginAttemptRepository loginAttemptRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptRepository = loginAttemptRepository;
    }

    public final LoginResponse verify(String email, String passWord, HttpServletRequest request) {
        String ipClient = getClientIP(request);

        User user; // ???
        try {
            user = userRepository.findUserByEmail(email);
        } catch (InvalidAccountException e) {
            loginAttemptRepository.addAttempt(new LoginAttempt(email, ipClient, false));
            throw new InvalidAccountException("Email không tồn tại");
        }

        // Check if account is active
        if (!user.getActive()) {
            throw new InvalidAccountException("Tài khoản chưa được kích hoạt. Vui lòng kiểm tra email.");
        }

        // Check if account is locked
        if (user.getLocked()) {
            if (LocalDateTime.now().isBefore(user.getLockedUntil())) {
                throw new InvaildAccountLockedException(
                        "Tài khoản đã bị khóa đến: ", user.getLockedUntil()
                );
            }
            user.setLocked(false);
            user.setCounterFail(0);
            user.setLockedUntil(null);
        }

        // Check rate limit: 5 failed attempts in 15 minutes
        long recentFailures = loginAttemptRepository.getFailedAttempts(
                email, LocalDateTime.now().minusMinutes(15)
        ).size();

        if (recentFailures >= 5) {
            user.setLocked(true);
            user.setLockedUntil(LocalDateTime.now().plusMinutes(15));
            throw new InvaildAccountLockedException(
                    "Quá nhiều lần đăng nhập sai. Tài khoản bị khóa 15 phút đến: ",
                    user.getLockedUntil()
            );
        }

        // Verify password
        if (!passwordEncoder.matches(passWord, user.getPassWord())) {
            user.setCounterFail(user.getCounterFail() + 1);
            loginAttemptRepository.addAttempt(new LoginAttempt(email, ipClient, false));

            if (user.getCounterFail() >= 5) {
                user.setLocked(true);
                user.setLockedUntil(LocalDateTime.now().plusMinutes(15));
                throw new InvaildAccountLockedException(
                        "Tài khoản đã bị khóa do nhập sai quá nhiều lần. Khóa đến: ",
                        user.getLockedUntil()
                );
            }

            throw new InvalidPassWordException("Mật khẩu sai");
        }

        // Login success
        loginAttemptRepository.addAttempt(new LoginAttempt(email, ipClient, true));
        user.setCounterFail(0);
        user.setLocked(false);
        user.setLockedUntil(null);

        String accessToken = JwtUtil.generateAccessToken(user.getEmail(), user.getRole());
        String refreshToken = JwtUtil.genarateRefreshToken(user.getEmail(), user.getRole());

        return new LoginResponse(accessToken, refreshToken, user.getRole());
    }

    public LoginResponse refreshAccessToken(String refreshToken) {
        Claims claims = JwtUtil.validateRefreshToken(refreshToken);

        String email = claims.getSubject();
        String role  = (String) claims.get("role");

        String newAccessToken = JwtUtil.generateAccessToken(email, role);

        return new LoginResponse(newAccessToken, refreshToken, role);
    }
}
