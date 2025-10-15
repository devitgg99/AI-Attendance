package com.example.attendancemanagement.service;

import com.example.attendancemanagement.config.EmailConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final EmailConfig emailConfig;
    private final RestTemplate restTemplate;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.name:Attendance Management System}")
    private String appName;

    /**
     * Send OTP via email using Thymeleaf template
     */
    public void sendOtpEmail(String toEmail, String otp) {
        String subject = appName + " - Password Reset OTP";
        
        // Prepare template context
        Context context = new Context();
        context.setVariable("otp", otp);
        context.setVariable("appName", appName);
        
        // Process template
        String htmlContent = templateEngine.process("email/otp-email", context);
        
        sendEmail(toEmail, subject, htmlContent);
    }

    /**
     * Send password reset confirmation email using Thymeleaf template
     */
    public void sendPasswordResetConfirmation(String toEmail) {
        String subject = appName + " - Password Reset Successful";
        
        // Prepare template context
        Context context = new Context();
        context.setVariable("appName", appName);
        
        // Process template
        String htmlContent = templateEngine.process("email/password-reset-confirmation", context);
        
        sendEmail(toEmail, subject, htmlContent);
    }

    /**
     * Main email sending method with provider selection and fallback
     */
    private void sendEmail(String toEmail, String subject, String htmlContent) {
        try {
            switch (emailConfig.getProvider()) {
                case SMTP:
                    sendViaSmtp(toEmail, subject, htmlContent);
                    break;
                case SENDGRID:
                    sendViaSendGrid(toEmail, subject, htmlContent);
                    break;
                case MAILGUN:
                    sendViaMailgun(toEmail, subject, htmlContent);
                    break;
                case DISABLED:
                    handleFallback(toEmail, subject, htmlContent, "Email sending is disabled");
                    break;
                default:
                    sendViaSmtp(toEmail, subject, htmlContent);
            }
        } catch (Exception e) {
            log.error("Failed to send email to {} via {}: {}", toEmail, emailConfig.getProvider(), e.getMessage());
            handleFallback(toEmail, subject, htmlContent, e.getMessage());
        }
    }

    /**
     * Send email via SMTP
     */
    private void sendViaSmtp(String toEmail, String subject, String htmlContent) throws MessagingException {
        if (fromEmail == null || fromEmail.trim().isEmpty()) {
            throw new RuntimeException("SMTP email configuration not properly set");
        }
        
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        
        mailSender.send(message);
        log.info("Email sent successfully via SMTP to: {}", toEmail);
    }

    /**
     * Send email via SendGrid API
     */
    private void sendViaSendGrid(String toEmail, String subject, String htmlContent) {
        if (emailConfig.getSendGrid().getApiKey() == null || emailConfig.getSendGrid().getApiKey().trim().isEmpty()) {
            throw new RuntimeException("SendGrid API key not configured");
        }

        String url = "https://api.sendgrid.com/v3/mail/send";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + emailConfig.getSendGrid().getApiKey());
        
        Map<String, Object> emailData = new HashMap<>();
        Map<String, Object> personalizations = new HashMap<>();
        personalizations.put("to", new Object[]{Map.of("email", toEmail)});
        personalizations.put("subject", subject);
        
        Map<String, Object> from = new HashMap<>();
        from.put("email", emailConfig.getSendGrid().getFromEmail());
        from.put("name", emailConfig.getSendGrid().getFromName());
        
        Map<String, Object> content = new HashMap<>();
        content.put("type", "text/html");
        content.put("value", htmlContent);
        
        emailData.put("personalizations", new Object[]{personalizations});
        emailData.put("from", from);
        emailData.put("content", new Object[]{content});
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(emailData, headers);
        
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Email sent successfully via SendGrid to: {}", toEmail);
        } else {
            throw new RuntimeException("SendGrid API returned: " + response.getStatusCode());
        }
    }

    /**
     * Send email via Mailgun API
     */
    private void sendViaMailgun(String toEmail, String subject, String htmlContent) {
        if (emailConfig.getMailgun().getApiKey() == null || emailConfig.getMailgun().getApiKey().trim().isEmpty()) {
            throw new RuntimeException("Mailgun API key not configured");
        }

        String url = "https://api.mailgun.net/v3/" + emailConfig.getMailgun().getDomain() + "/messages";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth("api", emailConfig.getMailgun().getApiKey());
        
        Map<String, String> formData = new HashMap<>();
        formData.put("from", emailConfig.getMailgun().getFromName() + " <" + emailConfig.getMailgun().getFromEmail() + ">");
        formData.put("to", toEmail);
        formData.put("subject", subject);
        formData.put("html", htmlContent);
        
        HttpEntity<Map<String, String>> request = new HttpEntity<>(formData, headers);
        
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Email sent successfully via Mailgun to: {}", toEmail);
        } else {
            throw new RuntimeException("Mailgun API returned: " + response.getStatusCode());
        }
    }

    /**
     * Handle fallback when email sending fails
     */
    private void handleFallback(String toEmail, String subject, String htmlContent, String errorMessage) {
        switch (emailConfig.getFallbackMode()) {
            case "log":
                log.warn("EMAIL FALLBACK - Could not send email to: {} | Subject: {} | Error: {}", 
                        toEmail, subject, errorMessage);
                log.warn("EMAIL CONTENT: {}", htmlContent);
                break;
            case "database":
                // TODO: Implement database storage for failed emails
                log.warn("EMAIL FALLBACK - Storing failed email in database for: {}", toEmail);
                break;
            case "disable":
                log.warn("EMAIL FALLBACK - Email sending disabled, skipping: {}", toEmail);
                break;
            default:
                log.error("EMAIL FAILED - No fallback configured for: {} | Error: {}", toEmail, errorMessage);
        }
    }

}
