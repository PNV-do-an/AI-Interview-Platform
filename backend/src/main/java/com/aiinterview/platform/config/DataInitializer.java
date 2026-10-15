package com.aiinterview.platform.config;

import com.aiinterview.platform.model.entity.ServicePackage;
import com.aiinterview.platform.model.entity.User;
import com.aiinterview.platform.model.enums.Role;
import com.aiinterview.platform.model.repository.ServicePackageRepository;
import com.aiinterview.platform.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seed một tài khoản Admin mặc định khi DB chưa có user nào,
 * và các gói dịch vụ mẫu khi chưa có gói nào.
 * Chỉ chạy một lần lúc startup; nếu đã có dữ liệu thì bỏ qua.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ServicePackageRepository servicePackageRepository;

    private static final String DEFAULT_ADMIN_EMAIL    = "admin@aiinterview.com";
    private static final String DEFAULT_ADMIN_PASSWORD = "Admin@12345";
    private static final String DEFAULT_ADMIN_NAME     = "Super Admin";

    @Override
    public void run(ApplicationArguments args) {
        seedAdmin();
        seedServicePackages();
    }

    private void seedAdmin() {
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

    private void seedServicePackages() {
        if (!servicePackageRepository.findAll().isEmpty()) {
            return;
        }

        List<ServicePackage> packages = List.of(
                ServicePackage.builder()
                        .name("Cơ bản")
                        .description("Dành cho doanh nghiệp nhỏ mới bắt đầu tuyển dụng.")
                        .price(499_000L)
                        .durationDays(30)
                        .jobPostLimit(3)
                        .features(List.of(
                                "Đăng tối đa 3 tin tuyển dụng",
                                "Nhận hồ sơ ứng tuyển không giới hạn",
                                "Gợi ý ứng viên phù hợp bằng AI",
                                "Hỗ trợ qua email"))
                        .active(true)
                        .build(),
                ServicePackage.builder()
                        .name("Chuyên nghiệp")
                        .description("Dành cho doanh nghiệp cần tuyển nhanh với công cụ AI mạnh hơn.")
                        .price(1_299_000L)
                        .durationDays(30)
                        .jobPostLimit(10)
                        .features(List.of(
                                "Đăng tối đa 10 tin tuyển dụng",
                                "Tin được ưu tiên hiển thị",
                                "Phỏng vấn thử AI 20 lượt/tháng",
                                "Phân tích CV ứng viên bằng AI",
                                "Hỗ trợ ưu tiên 24/7"))
                        .active(true)
                        .build(),
                ServicePackage.builder()
                        .name("Doanh nghiệp")
                        .description("Gói toàn diện cho doanh nghiệp tuyển dụng quy mô lớn.")
                        .price(2_999_000L)
                        .durationDays(90)
                        .jobPostLimit(50)
                        .features(List.of(
                                "Đăng tối đa 50 tin tuyển dụng",
                                "Tin nổi bật trang chủ",
                                "Phỏng vấn thử AI không giới hạn",
                                "Phân tích CV & thư xin việc AI",
                                "Quản lý đội ngũ phỏng vấn",
                                "Chuyên viên hỗ trợ riêng"))
                        .active(true)
                        .build());

        servicePackageRepository.saveAll(packages);
        log.info("=== Seeded {} service packages ===", packages.size());
    }
}
