package com.aiinterview.platform.service.impl;

import com.aiinterview.platform.common.exception.InvalidAccountException;
import com.aiinterview.platform.common.exception.InvalidPasswordException;
import com.aiinterview.platform.model.dto.response.RegisterResponse;
import com.aiinterview.platform.model.entity.RegisterAttempt;
import com.aiinterview.platform.model.entity.User;
import com.aiinterview.platform.model.enums.Role;
import com.aiinterview.platform.model.repository.RegisterAttemptRepository;
import com.aiinterview.platform.model.repository.UserRepository;
import com.aiinterview.platform.security.utils.SecurityUtils;
import com.aiinterview.platform.service.EmailService;
import com.aiinterview.platform.service.RegisterInterface;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class RegisterInterfaceImpl implements RegisterInterface {

    private final UserRepository userRepository;
    private final RegisterAttemptRepository registerAttemptRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$");

    @Override
    public RegisterResponse register(String email, String password, String fullName, String phone,
                                     HttpServletRequest request) {
        String ipClient = SecurityUtils.getClientIP(request);

        // Bước 1: Check IP rate-limit — 10 lần thất bại từ IP này trong 5 phút
        long ipFailures = registerAttemptRepository.countFailedByIp(ipClient, LocalDateTime.now().minusMinutes(5));
        if (ipFailures >= 10) {
            throw new InvalidPasswordException(
                    "Hệ thống phát hiện hoạt động bất thường từ thiết bị của bạn. Vui lòng thử lại sau."
            );
        }

        // Bước 2: Check email rate-limit — 5 lần thất bại từ email này
        RegisterAttempt emailAttempt = registerAttemptRepository.findRegisterAttemptByEmail(email);
        if (emailAttempt == null) {
            emailAttempt = new RegisterAttempt(ipClient, email);
            emailAttempt.setTimestamp(LocalDateTime.now());
            registerAttemptRepository.addRegisterAttempt(emailAttempt);
        }
        if (emailAttempt.getLocked() != null && emailAttempt.getLocked()) {
            if (LocalDateTime.now().isBefore(emailAttempt.getLockUntil())) {
                throw new InvalidPasswordException(
                        "Email này đã thực hiện quá nhiều lần đăng ký thất bại. Vui lòng thử lại sau 5 phút."
                );
            }
            emailAttempt.setLocked(false);
            emailAttempt.setCounterFail(0);
            emailAttempt.setLockUntil(null);
        }

        // Bước 3: Check IP-based lock (giữ từ code cũ)
        RegisterAttempt ipAttempt = registerAttemptRepository.findRegisterAttempt(ipClient);
        if (ipAttempt == null) {
            ipAttempt = new RegisterAttempt(ipClient, email);
            ipAttempt.setTimestamp(LocalDateTime.now());
            registerAttemptRepository.addRegisterAttempt(ipAttempt);
        }
        if (ipAttempt.getLocked() != null && ipAttempt.getLocked()) {
            if (LocalDateTime.now().isBefore(ipAttempt.getLockUntil())) {
                throw new InvalidPasswordException(
                        "Hệ thống phát hiện hoạt động bất thường từ thiết bị của bạn. Vui lòng thử lại sau."
                );
            }
            ipAttempt.setLocked(false);
            ipAttempt.setCounterFail(0);
            ipAttempt.setLockUntil(null);
        }

        if (userRepository.existsByEmail(email)) {
            throw new InvalidAccountException("Email đã tồn tại trong hệ thống");
        }

        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            // Tăng counter cho cả email và IP
            emailAttempt.setCounterFail(emailAttempt.getCounterFail() + 1);
            ipAttempt.setCounterFail(ipAttempt.getCounterFail() + 1);

            if (emailAttempt.getCounterFail() >= 5) {
                emailAttempt.setLocked(true);
                emailAttempt.setLockUntil(LocalDateTime.now().plusMinutes(5));
            }
            if (ipAttempt.getCounterFail() >= 10) {
                ipAttempt.setLocked(true);
                ipAttempt.setLockUntil(LocalDateTime.now().plusMinutes(5));
            }

            throw new InvalidPasswordException(
                    "Mật khẩu phải có tối thiểu 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt (@#$%^&+=!)"
            );
        }

        String verificationToken = UUID.randomUUID().toString();

        User newUser = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .fullName(fullName)
                .phone(phone)
                .role(Role.ROLE_USER)
                .enabled(false)
                .verificationToken(verificationToken)
                .verificationTokenExpiry(LocalDateTime.now().plusHours(24))
                .build();

        userRepository.save(newUser);

        // Reset counters sau khi đăng ký thành công
        emailAttempt.setCounterFail(0);
        emailAttempt.setLocked(false);
        emailAttempt.setLockUntil(null);
        ipAttempt.setCounterFail(0);
        ipAttempt.setLocked(false);
        ipAttempt.setLockUntil(null);

        emailService.sendVerificationEmail(email, fullName, verificationToken);

        return new RegisterResponse("Đăng ký thành công! Vui lòng kiểm tra email để kích hoạt tài khoản.");
    }

    @Override
    public String verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new InvalidAccountException("Token xác thực không hợp lệ"));

        if (LocalDateTime.now().isAfter(user.getVerificationTokenExpiry())) {
            throw new InvalidAccountException("Link xác thực đã hết hạn (quá 24 giờ)");
        }

        user.setEnabled(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);

        return "Tài khoản đã được kích hoạt thành công! Bạn có thể đăng nhập ngay.";
    }

    @Override
    public void resendVerification(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidAccountException("Email không tồn tại trong hệ thống"));

        if (user.isEnabled()) {
            throw new InvalidAccountException("Tài khoản đã được kích hoạt");
        }

        String newToken = UUID.randomUUID().toString();
        user.setVerificationToken(newToken);
        user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));
        userRepository.save(user);

        emailService.sendResendVerificationEmail(email, user.getFullName(), newToken);
    }
}
