package com.aiinterview.platform.service.impl;

import com.aiinterview.platform.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public void sendVerificationEmail(String to, String fullName, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Xác thực tài khoản AI Interview Platform");
        message.setText("Xin chào " + fullName + ",\n\n"
                + "Vui lòng xác thực tài khoản bằng cách nhấn vào link sau:\n"
                + "http://localhost:8080/api/v1/auth/verify?token=" + token + "\n\n"
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
                + "http://localhost:8080/api/v1/auth/verify?token=" + token + "\n\n"
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
                + frontendUrl + "/reset-password?token=" + token + "\n\n"
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
    public void sendAccountUnlockedEmail(String to, String fullName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Tài khoản của bạn đã được mở khóa");
        message.setText("Xin chào " + fullName + ",\n\n"
                + "Tài khoản của bạn trên AI Interview Platform đã được quản trị viên mở khóa.\n"
                + "Bây giờ bạn có thể đăng nhập lại bình thường.");
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

    @Override
    public void sendApplicationReceivedEmail(String to, String candidateName, String jobTitle) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Có ứng viên ứng tuyển: " + jobTitle);
            message.setText("Xin chào,\n\n"
                    + "Ứng viên " + candidateName + " vừa ứng tuyển vào vị trí \"" + jobTitle + "\".\n"
                    + "Vui lòng đăng nhập hệ thống để xem chi tiết và sắp xếp lịch phỏng vấn.");
            mailSender.send(message);
        } catch (Exception e) {
            // notification email must not break the main flow
        }
    }

    @Override
    public void sendApplicationStatusEmail(String to, String candidateName, String jobTitle, String statusLabel) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Cập nhật trạng thái ứng tuyển: " + jobTitle);
            message.setText("Xin chào " + candidateName + ",\n\n"
                    + "Đơn ứng tuyển của bạn vào vị trí \"" + jobTitle + "\" đã chuyển sang trạng thái: " + statusLabel + ".\n"
                    + "Vui lòng đăng nhập hệ thống để xem chi tiết.");
            mailSender.send(message);
        } catch (Exception e) {
            // notification email must not break the main flow
        }
    }

    @Override
    public void sendSlotBookedEmail(String to, String name, String jobTitle, String startTime, String role) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Xác nhận lịch phỏng vấn: " + jobTitle);
            message.setText("Xin chào " + name + ",\n\n"
                    + "Lịch phỏng vấn cho vị trí \"" + jobTitle + "\" vào lúc " + startTime
                    + " đã được xác nhận (" + role + ").\n"
                    + "Vui lòng tham gia đúng giờ.");
            mailSender.send(message);
        } catch (Exception e) {
            // notification email must not break the main flow
        }
    }

    @Override
    public void sendOrderConfirmationEmail(String to, String fullName, String packageName, Long amount, String orderCode) {
        try {
            String amountText = String.format("%,d", amount).replace(',', '.');
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Xác nhận thanh toán đơn hàng " + orderCode);
            message.setText("Xin chào " + fullName + ",\n\n"
                    + "Thanh toán của bạn đã thành công.\n"
                    + "Mã đơn hàng: " + orderCode + "\n"
                    + "Gói dịch vụ: " + packageName + "\n"
                    + "Số tiền: " + amountText + " VND\n\n"
                    + "Cảm ơn bạn đã sử dụng AI Interview Platform!");
            mailSender.send(message);
        } catch (Exception e) {
            // notification email must not break the main flow
        }
    }
}
