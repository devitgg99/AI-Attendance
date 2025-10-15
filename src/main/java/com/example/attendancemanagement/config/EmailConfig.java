package com.example.attendancemanagement.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "email")
public class EmailConfig {
    
    private Provider provider = Provider.SMTP;
    private String fallbackMode = "log"; // log, database, disable
    
    // SMTP Configuration
    private Smtp smtp = new Smtp();
    
    // SendGrid Configuration
    private SendGrid sendGrid = new SendGrid();
    
    // Mailgun Configuration
    private Mailgun mailgun = new Mailgun();
    
    @Data
    public static class Smtp {
        private String host;
        private int port = 587;
        private String username;
        private String password;
        private boolean auth = true;
        private boolean starttls = true;
        private boolean ssl = false;
        private int timeout = 10000;
    }
    
    @Data
    public static class SendGrid {
        private String apiKey;
        private String fromEmail;
        private String fromName;
    }
    
    @Data
    public static class Mailgun {
        private String apiKey;
        private String domain;
        private String fromEmail;
        private String fromName;
    }
    
    public enum Provider {
        SMTP, SENDGRID, MAILGUN, DISABLED
    }
}
