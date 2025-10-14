package com.example.attendancemanagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.name:Attendance Management System}")
    private String appName;

    /**
     * Send OTP via email using Thymeleaf template
     */
    public void sendOtpEmail(String toEmail, String otp) {
        // Check if email configuration is properly set
        if (fromEmail == null || fromEmail.trim().isEmpty()) {
            log.error("Email configuration not set. Cannot send OTP email to: {}", toEmail);
            throw new RuntimeException("Email configuration not properly set. Please configure SMTP settings.");
        }
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            // Set email properties
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(appName + " - Password Reset OTP");
            
            // Prepare template context
            Context context = new Context();
            context.setVariable("otp", otp);
            context.setVariable("appName", appName);
            
            // Process template
            String htmlContent = templateEngine.process("email/otp-email", context);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("OTP email sent successfully to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send OTP email: " + e.getMessage(), e);
        }
    }

    /**
     * Send password reset confirmation email using Thymeleaf template
     */
    public void sendPasswordResetConfirmation(String toEmail) {
        try {
            // Check if email configuration is properly set
            if (fromEmail == null || fromEmail.trim().isEmpty()) {
                log.warn("Email configuration not set. Skipping confirmation email to: {}", toEmail);
                return;
            }
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            // Set email properties
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(appName + " - Password Reset Successful");
            
            // Prepare template context
            Context context = new Context();
            context.setVariable("appName", appName);
            
            // Process template
            String htmlContent = templateEngine.process("email/password-reset-confirmation", context);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("Password reset confirmation email sent to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send password reset confirmation to {}: {}", toEmail, e.getMessage());
            // Don't throw exception for confirmation email
        }
    }

}
