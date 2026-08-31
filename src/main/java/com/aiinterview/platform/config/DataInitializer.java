package com.aiinterview.platform.config;

import com.aiinterview.platform.model.entity.User;
import com.aiinterview.platform.model.enums.Role;
import com.aiinterview.platform.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seed một tài khoản Admin mặc định khi DB chưa có user nào.
 * Chỉ chạy một lần lúc startup; nếu đã có admin thì bỏ qua.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String DEFAULT_ADMIN_EMAIL    = "admin@aiinterview.com";
    private static final String DEFAULT_ADMIN_PASSWORD = "Admin@12345";
    private static final String DEFAULT_ADMIN_NAME     = "Super Admin";

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.existsByEmail(DEFAULT_ADMIN_EMAIL)) {
            log.info("Admin account already exists — skipping seed.");
            return;
        }

        User admin = User.builder()
                .email(DEFAULT_ADMIN_EMAIL)
                .password(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD))
                .fullName(DEFAULT_ADMIN_NAME)
                .role(Role.ROLE_ADMIN)
                .enabled(true)
                .locked(false)
                .build();

        userRepository.save(admin);
        log.info("=== Default admin created: {} / {} ===", DEFAULT_ADMIN_EMAIL, DEFAULT_ADMIN_PASSWORD);
    }
}
