package com.aiinterview.platform.controller.Register;

import com.aiinterview.platform.model.entity.User;
import com.aiinterview.platform.model.repository.FakeUserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class VerificationController {

    private final FakeUserRepository userRepository;

    public VerificationController(FakeUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        for (User user : userRepository.userArrayList) {
            if (token.equals(user.getVerificationToken())) {
                if (LocalDateTime.now().isAfter(user.getVerificationTokenExpiry())) {
                    return ResponseEntity.status(410).body("Link xác thực đã hết hạn (quá 24 giờ)");
                }
                user.setActive(true);
                user.setVerificationToken(null);
                user.setVerificationTokenExpiry(null);
                System.out.println("=== ACCOUNT ACTIVATED: " + user.getEmail() + " ===");
                return ResponseEntity.ok("Tài khoản đã được kích hoạt thành công! Bạn có thể đăng nhập ngay.");
            }
        }
        return ResponseEntity.status(404).body("Token xác thực không hợp lệ");
    }
}
