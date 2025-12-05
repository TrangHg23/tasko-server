package com.hoangtrang.taskoserver.service.impl;

import com.hoangtrang.taskoserver.service.EmailService;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailServiceImpl implements EmailService {

    final JavaMailSender mailSender;

    @Value("${FRONTEND_URL}")
    String baseUrl;

    @Value("${MAIL_USERNAME}")
    String emailFrom;

    @Override
    @Async
    public void sendPasswordResetEmail(String to, String token) {
        try {
            String resetUrl = baseUrl + "/account/reset-password?token=" + token;

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String htmlContent =
                "<div style='font-family: Arial, sans-serif; padding: 20px;'>"
                    + "<h2>Password Reset Request</h2>"
                    + "<p>Click the button below to reset your password:</p>"
                    + "<a href='" + resetUrl + "' "
                    + "style='display: inline-block; padding: 10px 20px; "
                    + "background-color: #007bff; color: white; "
                    + "text-decoration: none; border-radius: 5px;'>"
                    + "Reset Password</a>"
                    + "<p>If you didn’t request this, you can ignore this email.</p>"
                    + "</div>";

            helper.setText(htmlContent, true);
            helper.setTo(to);
            helper.setSubject("Password Reset Request");
            helper.setFrom(new InternetAddress(emailFrom, "Tasko Support"));

            mailSender.send(mimeMessage);

        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", to, e.getMessage(), e);
        }
    }

}
