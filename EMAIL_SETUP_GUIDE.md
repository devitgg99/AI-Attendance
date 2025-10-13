# Email Configuration Guide

## Gmail SMTP Setup

### 1. Enable 2-Factor Authentication

- Go to [Google Account Security](https://myaccount.google.com/security)
- Enable 2-Factor Authentication if not already enabled

### 2. Generate App Password

- Go to [App Passwords](https://myaccount.google.com/apppasswords)
- Select "Mail" and "Other (Custom name)"
- Enter "Attendance Management System" as the name
- Copy the generated 16-character password

### 3. Update application.properties

```properties
# Email Configuration (Gmail SMTP)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-16-character-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.ssl.trust=smtp.gmail.com
spring.mail.properties.mail.smtp.connectiontimeout=5000
spring.mail.properties.mail.smtp.timeout=3000
spring.mail.properties.mail.smtp.writetimeout=5000
```

### 4. Alternative Email Providers

#### Outlook/Hotmail

```properties
spring.mail.host=smtp-mail.outlook.com
spring.mail.port=587
spring.mail.username=your-email@outlook.com
spring.mail.password=your-password
```

#### Yahoo Mail

```properties
spring.mail.host=smtp.mail.yahoo.com
spring.mail.port=587
spring.mail.username=your-email@yahoo.com
spring.mail.password=your-app-password
```

## Testing Email Configuration

### 1. Test Connection

```bash
# Test SMTP connection
telnet smtp.gmail.com 587
```

### 2. Check Logs

Look for these log messages:

- `OTP email sent successfully to: user@example.com`
- `Email configuration not set. Skipping OTP email to: user@example.com`

## Troubleshooting

### Common Issues:

1. **535 Authentication Failed**: Wrong username/password or 2FA not enabled
2. **Connection timeout**: Firewall blocking port 587
3. **SSL errors**: Wrong SSL configuration

### Solutions:

1. Use App Password instead of regular password
2. Check firewall settings
3. Verify SSL/TLS settings
4. Test with different email provider
