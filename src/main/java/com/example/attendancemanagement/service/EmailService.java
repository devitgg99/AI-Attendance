package com.example.attendancemanagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.name:Attendance Management System}")
    private String appName;

    /**
     * Send OTP via email
     */
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            // Check if email configuration is properly set
            if (fromEmail == null || fromEmail.trim().isEmpty()) {
                log.warn("Email configuration not set. Skipping OTP email to: {}", toEmail);
                return;
            }
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(appName + " - Password Reset OTP");
            message.setText(buildOtpEmailBody(otp));
            
            mailSender.send(message);
            log.info("OTP email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage());
            // Don't throw exception to prevent breaking the forgot password flow
            // The OTP will still be sent via FCM as fallback
        }
    }

    /**
     * Send password reset confirmation email
     */
    public void sendPasswordResetConfirmation(String toEmail) {
        try {
            // Check if email configuration is properly set
            if (fromEmail == null || fromEmail.trim().isEmpty()) {
                log.warn("Email configuration not set. Skipping confirmation email to: {}", toEmail);
                return;
            }
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(appName + " - Password Reset Successful");
            message.setText(buildPasswordResetConfirmationBody());
            
            mailSender.send(message);
            log.info("Password reset confirmation email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password reset confirmation to {}: {}", toEmail, e.getMessage());
            // Don't throw exception for confirmation email
        }
    }

    private String buildOtpEmailBody(String otp) {
        return String.format("""
            Dear User,
            
            You have requested to reset your password for %s.
            
            Your OTP (One-Time Password) is: %s
            
            This OTP is valid for 5 minutes only.
            
            If you did not request this password reset, please ignore this email.
            
            Best regards,
            %s Team
            """, appName, otp, appName);
    }

    private String buildPasswordResetConfirmationBody() {
        return String.format("""
            Dear User,
            
            Your password has been successfully reset for %s.
            
            If you did not make this change, please contact our support team immediately.
            
            Best regards,
            %s Team
            """, appName, appName);
    }
}
