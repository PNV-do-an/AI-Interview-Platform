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
    public void sendWelcomeEmail(String to, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Welcome to AI Interview Platform!");
        message.setText("Hello " + username + ",\n\nWelcome to AI Interview Platform!");
        mailSender.send(message);
    }

    @Override
    public void sendVerificationEmail(String to, String token) {
        sendVerificationEmail(to, token, null);
    }

    @Override
    public void sendVerificationEmail(String to, String token, String fullName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Email Verification");
        String greeting = fullName != null ? "Hello " + fullName + ",\n\n" : "";
        message.setText(greeting + "Please verify your email by clicking: http://localhost:8080/api/verify?token=" + token);
        mailSender.send(message);
    }

    @Override
    public void sendPasswordResetEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset Request");
        message.setText("Click here to reset your password: http://localhost:8080/api/reset-password?token=" + token);
        mailSender.send(message);
    }

    @Override
    public void sendResetPasswordEmail(String to, String token, String fullName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset Request");
        String greeting = fullName != null ? "Hello " + fullName + ",\n\n" : "";
        message.setText(greeting + "Click here to reset your password: http://localhost:8080/api/reset-password?token=" + token);
        mailSender.send(message);
    }

    @Override
    public void sendNotificationEmail(String to, String messageContent) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Notification from AI Interview Platform");
        message.setText(messageContent);
        mailSender.send(message);
    }

    @Override
    public void sendAccountLockedEmail(String to, String fullName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Account Locked Notification");
        String greeting = fullName != null ? "Hello " + fullName + ",\n\n" : "";
        message.setText(greeting + "Your account has been locked by an administrator. Please contact support for assistance.");
        mailSender.send(message);
    }

    @Override
    public void sendAccountDeletedEmail(String to, String fullName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Account Deleted Notification");
        String greeting = fullName != null ? "Hello " + fullName + ",\n\n" : "";
        message.setText(greeting + "Your account has been deleted by an administrator. If you think this is a mistake, please contact support.");
        mailSender.send(message);
    }

    @Override
    public void sendPasswordResetConfirmation(String to, String fullName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset Confirmation");
        String greeting = fullName != null ? "Hello " + fullName + ",\n\n" : "";
        message.setText(greeting + "Your password has been successfully reset. If you did not perform this action, please contact support immediately.");
        mailSender.send(message);
    }

    @Override
    public void sendResendVerificationEmail(String to, String token, String fullName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Resend Email Verification");
        String greeting = fullName != null ? "Hello " + fullName + ",\n\n" : "";
        message.setText(greeting + "Please verify your email by clicking: http://localhost:8080/api/verify?token=" + token);
        mailSender.send(message);
    }
}