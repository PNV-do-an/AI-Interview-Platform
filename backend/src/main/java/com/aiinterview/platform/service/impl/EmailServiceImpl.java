package com.aiinterview.platform.service.impl;

import com.aiinterview.platform.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendVerificationEmail(String to, String fullName, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Xác thực tài khoản AI Interview Platform");
        message.setText("Xin chào " + fullName + ",\n\n"
                + "Vui lòng xác thực tài khoản bằng cách nhấn vào link sau:\n"
                + "http://localhost:8080/api/v1/auth/verify-email?token=" + token + "\n\n"
                + "Link có hiệu lực trong 24 giờ.");
        mailSender.send(message);
    }

    @Override
    public void sendResendVerificationEmail(String to, String fullName, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Gửi lại xác thực tài khoản AI Interview Platform");
        message.setText("Xin chào " + fullName + ",\n\n"
                + "Đây là link xác thực mới của bạn:\n"
                + "http://localhost:8080/api/v1/auth/verify-email?token=" + token + "\n\n"
                + "Link có hiệu lực trong 24 giờ.");
        mailSender.send(message);
    }

    @Override
    public void sendResetPasswordEmail(String to, String fullName, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Đặt lại mật khẩu AI Interview Platform");
        message.setText("Xin chào " + fullName + ",\n\n"
                + "Bạn đã yêu cầu đặt lại mật khẩu. Nhấn vào link sau:\n"
                + "http://localhost:3000/reset-password?token=" + token + "\n\n"
                + "Link có hiệu lực trong 1 giờ. Nếu bạn không yêu cầu điều này, hãy bỏ qua email này.");
        mailSender.send(message);
    }

    @Override
    public void sendPasswordResetConfirmation(String to, String fullName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Mật khẩu đã được đặt lại thành công");
        message.setText("Xin chào " + fullName + ",\n\n"
                + "Mật khẩu của bạn đã được đặt lại thành công.\n"
                + "Nếu bạn không thực hiện điều này, vui lòng liên hệ ngay với chúng tôi.");
        mailSender.send(message);
    }

    @Override
    public void sendAccountLockedEmail(String to, String fullName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Tài khoản của bạn đã bị khóa");
        message.setText("Xin chào " + fullName + ",\n\n"
                + "Tài khoản của bạn trên AI Interview Platform đã bị khóa bởi quản trị viên.\n"
                + "Vui lòng liên hệ hỗ trợ để biết thêm thông tin.");
        mailSender.send(message);
    }

    @Override
    public void sendAccountDeletedEmail(String to, String fullName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Tài khoản của bạn đã bị xóa");
        message.setText("Xin chào " + fullName + ",\n\n"
                + "Tài khoản của bạn trên AI Interview Platform đã bị xóa bởi quản trị viên.\n"
                + "Vui lòng liên hệ hỗ trợ nếu bạn có thắc mắc.");
        mailSender.send(message);
    }
}
