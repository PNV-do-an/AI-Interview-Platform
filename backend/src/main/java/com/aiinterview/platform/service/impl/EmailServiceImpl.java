package com.aiinterview.platform.service.impl;

import com.aiinterview.platform.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Override
    public void sendVerificationEmail(String to, String fullName, String token) {
        String verifyLink = baseUrl + "/api/v1/auth/verify?token=" + token;
        sendHtmlEmail(to,
                "Xác thực tài khoản - AI Interview Platform",
                buildVerificationHtml(fullName, verifyLink)
        );
    }


    @Override
    public void sendResendVerificationEmail(String to, String fullName, String token) {
        String verifyLink = baseUrl + "/api/v1/auth/verify?token=" + token;
        sendHtmlEmail(to,
                "Xác thực lại tài khoản - AI Interview Platform",
                buildResendHtml(fullName, verifyLink)
        );
    }

    private void sendHtmlEmail(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
            System.out.println(">>> Email sent to: " + to);
        } catch (MessagingException e) {
            System.err.println(">>> Failed to send email to: " + to + " - " + e.getMessage());
            throw new RuntimeException("Khong the gui email xac thuc", e);
        }
    }

    private String buildVerificationHtml(String fullName, String verifyLink) {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'/></head>" +
                "<body style='font-family: Arial, sans-serif; padding: 20px;'>" +
                "<h2>Chào " + fullName + ",</h2>" +
                "<p>Cảm ơn bạn đã đăng ký tài khoản tại <strong>AI Interview Platform</strong>.</p>" +
                "<p>Vui lòng nhấn vào nút bên dưới để xác thực email của bạn:</p>" +
                "<a href='" + verifyLink + "' style='display: inline-block; padding: 12px 24px; " +
                "background-color: #4CAF50; color: white; text-decoration: none; border-radius: 4px;'>Xác thực ngay</a>" +
                "<p style='margin-top: 20px;'>Hoặc copy link sau vào trình duyệt:</p>" +
                "<p style='color: #666;'>" + verifyLink + "</p>" +
                "<hr><p style='color: #999; font-size: 12px;'>Link có hiệu lực trong 24 giờ.</p>" +
                "</body></html>";
    }

    private String buildResendHtml(String fullName, String verifyLink) {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'/></head>" +
                "<body style='font-family: Arial, sans-serif; padding: 20px;'>" +
                "<h2>Chào " + fullName + ",</h2>" +
                "<p>Bạn vừa yêu cầu gửi lại link xác thực tại <strong>AI Interview Platform</strong>.</p>" +
                "<p>Nhấn nút bên dưới để xác thực email:</p>" +
                "<a href='" + verifyLink + "' style='display: inline-block; padding: 12px 24px; " +
                "background-color: #4CAF50; color: white; text-decoration: none; border-radius: 4px;'>Xác thực ngay</a>" +
                "<p style='margin-top: 20px;'>Hoặc copy link sau vào trình duyệt:</p>" +
                "<p style='color: #666;'>" + verifyLink + "</p>" +
                "<hr><p style='color: #999; font-size: 12px;'>Link có hiệu lực trong 24 giờ.</p>" +
                "</body></html>";
    }

    @Override
    public void sendResetPasswordEmail(String to, String fullName, String token) {
        String resetLink = baseUrl + "/api/v1/auth/reset-password?token=" + token;
        sendHtmlEmail(to,
                "Đặt lại mật khẩu - AI Interview Platform",
                buildResetPasswordHtml(fullName, resetLink)
        );
    }

    @Override
    public void sendPasswordResetConfirmation(String to, String fullName) {
        sendHtmlEmail(to,
                "Mật khẩu đã được đặt lại - AI Interview Platform",
                buildResetConfirmationHtml(fullName)
        );
    }

    private String buildResetPasswordHtml(String fullName, String resetLink) {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'/></head>" +
                "<body style='font-family: Arial, sans-serif; padding: 20px;'>" +
                "<h2>Chào " + fullName + ",</h2>" +
                "<p>Bạn vừa yêu cầu đặt lại mật khẩu tại <strong>AI Interview Platform</strong>.</p>" +
                "<p>Nhấn nút bên dưới để đặt lại mật khẩu:</p>" +
                "<a href='" + resetLink + "' style='display: inline-block; padding: 12px 24px; " +
                "background-color: #2196F3; color: white; text-decoration: none; border-radius: 4px;'>Đặt lại mật khẩu</a>" +
                "<p style='margin-top: 20px;'>Hoặc copy link sau vào trình duyệt:</p>" +
                "<p style='color: #666;'>" + resetLink + "</p>" +
                "<hr><p style='color: #999; font-size: 12px;'>Link có hiệu lực trong 1 giờ. Nếu bạn không yêu cầu, hãy bỏ qua email này.</p>" +
                "</body></html>";
    }

    private String buildResetConfirmationHtml(String fullName) {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'/></head>" +
                "<body style='font-family: Arial, sans-serif; padding: 20px;'>" +
                "<h2>Chào " + fullName + ",</h2>" +
                "<p>Mật khẩu của bạn tại <strong>AI Interview Platform</strong> đã được đặt lại thành công.</p>" +
                "<p>Nếu bạn không thực hiện yêu cầu này, vui lòng liên hệ hỗ trợ ngay.</p>" +
                "<hr><p style='color: #999; font-size: 12px;'>Vui lòng đăng nhập lại với mật khẩu mới.</p>" +
                "</body></html>";
    }
}
