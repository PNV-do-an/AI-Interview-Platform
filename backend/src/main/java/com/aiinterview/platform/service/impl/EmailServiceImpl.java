package com.aiinterview.platform.service.impl;

import com.aiinterview.platform.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public void sendAccountLockedEmail(String toEmail, String fullName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Tài khoản của bạn đã bị khóa - AI Interview Platform");
            message.setText(String.format(
                "Xin chào %s,\n\n" +
                "Tài khoản của bạn đã bị khóa bởi quản trị viên.\n" +
                "Bạn sẽ không thể đăng nhập cho đến khi tài khoản được mở khóa.\n\n" +
                "Nếu bạn cho rằng đây là nhầm lẫn, vui lòng liên hệ quản trị viên.\n\n" +
                "Trân trọng,\nAI Interview Platform",
                fullName
            ));
            mailSender.send(message);
            log.info("Account locked email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send account locked email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Override
    public void sendAccountDeletedEmail(String toEmail, String fullName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Tài khoản của bạn đã bị xóa - AI Interview Platform");
            message.setText(String.format(
                "Xin chào %s,\n\n" +
                "Tài khoản của bạn đã bị xóa bởi quản trị viên.\n" +
                "Nếu bạn cho rằng đây là nhầm lẫn, vui lòng liên hệ quản trị viên.\n\n" +
                "Trân trọng,\nAI Interview Platform",
                fullName
            ));
            mailSender.send(message);
            log.info("Account deleted email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send account deleted email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String fullName, String resetToken) {
        try {
            String resetLink = frontendUrl + "/reset-password?token=" + resetToken;
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Đặt lại mật khẩu - AI Interview Platform");
            message.setText(String.format(
                "Xin chào %s,\n\n" +
                "Quản trị viên đã yêu cầu đặt lại mật khẩu cho tài khoản của bạn.\n" +
                "Nhấp vào liên kết sau để đặt lại mật khẩu (có hiệu lực trong 24 giờ):\n\n" +
                "%s\n\n" +
                "Nếu bạn không yêu cầu, hãy bỏ qua email này.\n\n" +
                "Trân trọng,\nAI Interview Platform",
                fullName, resetLink
            ));
            mailSender.send(message);
            log.info("Password reset email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage());
        }
    }
}
