package com.hoc.backend.SpringBoot.service.InterviewPlatform;

import com.hoc.backend.SpringBoot.dto.ResgiterResponse;
import com.hoc.backend.SpringBoot.exception.InvalidAccountException;
import com.hoc.backend.SpringBoot.exception.InvalidPassWordException;
import com.hoc.backend.SpringBoot.model.RegisterAttempt;
import com.hoc.backend.SpringBoot.model.User;
import com.hoc.backend.SpringBoot.repository.RegisterAttemptRepository;
import com.hoc.backend.SpringBoot.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

import static com.hoc.backend.SpringBoot.security.utils.SecurityUtils.getClientIP;

@Service
public class RegisterService {
    private final UserRepository userRepository;
    private final RegisterAttemptRepository registerAttemptRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$");

    public RegisterService(UserRepository userRepository,
                           RegisterAttemptRepository registerAttemptRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.registerAttemptRepository = registerAttemptRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResgiterResponse resgiter (String email, String passWord, String fullName, String phone,
                                      HttpServletRequest request) {

        String ipClient = getClientIP(request);

        RegisterAttempt attempt = registerAttemptRepository.findRegisterAttempt(ipClient);
        if (attempt == null) {
            attempt = new RegisterAttempt(ipClient, email);
            registerAttemptRepository.addRegisterAttempt(attempt);
        }

        if (attempt.getLocked() != null && attempt.getLocked()) {
            if (LocalDateTime.now().isBefore(attempt.getLockUntil())) {
                throw new InvalidPassWordException("Quá nhiều lần đăng ký thất bại. Vui lòng thử lại sau.");
            }
            attempt.setLocked(false);
            attempt.setCounterFail(0);
            attempt.setLockUntil(null);
        }

        if (userRepository.checkExistUser(email)) {
            throw new InvalidAccountException("Email đã tồn tại trong hệ thống");
        }

        if (passWord == null || !PASSWORD_PATTERN.matcher(passWord).matches()) {
            attempt.setCounterFail(attempt.getCounterFail() + 1);

            if (attempt.getCounterFail() >= 5) {
                attempt.setLocked(true);
                attempt.setLockUntil(LocalDateTime.now().plusMinutes(5));
            }

            throw new InvalidPassWordException(
                    "Mật khẩu phải có tối thiểu 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt (@#$%^&+=!)"
            );
        }

        User newUser = new User(
                userRepository.generateIdUser(),
                email,
                passwordEncoder.encode(passWord),
                "customer"
        );
        newUser.setFullName(fullName);
        newUser.setPhone(phone);
        newUser.setActive(false);

        String verificationToken = UUID.randomUUID().toString();
        newUser.setVerificationToken(verificationToken);
        newUser.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));

        userRepository.addUser(newUser);

        attempt.setIdUser(newUser.getId());
        attempt.setCounterFail(0);
        attempt.setLocked(false);
        attempt.setLockUntil(null);

        String verifyLink = "http://localhost:8080/api/verify?token=" + verificationToken;
        System.out.println("=== EMAIL VERIFICATION ===");
        System.out.println("To: " + email);
        System.out.println("Click to verify: " + verifyLink);
        System.out.println("=== END ===");

        return new ResgiterResponse("Đăng ký thành công! Vui lòng kiểm tra email để kích hoạt tài khoản.");
    }
}
