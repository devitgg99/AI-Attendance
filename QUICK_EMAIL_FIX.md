# Quick Email Fix for Hosting Platforms

## Immediate Solutions to Try

### 1. Try Different SMTP Port (Quickest Fix)

Update your `application.properties` file:

```properties
# Change from port 587 to 25
spring.mail.port=25
spring.mail.properties.mail.smtp.starttls.enable=false
spring.mail.properties.mail.smtp.ssl.enable=false
```

If port 25 doesn't work, try:

```properties
# Try port 2525
spring.mail.port=2525
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.ssl.enable=false
```

### 2. Use SendGrid (Recommended for Production)

1. **Sign up for SendGrid** (free tier: 100 emails/day)

   - Go to https://sendgrid.com
   - Create free account
   - Verify your email

2. **Get API Key**

   - Go to Settings > API Keys
   - Create API Key with "Mail Send" permissions
   - Copy the API key

3. **Update your application.properties**

```properties
# Replace SMTP configuration with SendGrid
spring.mail.host=smtp.sendgrid.net
spring.mail.port=587
spring.mail.username=apikey
spring.mail.password=YOUR_SENDGRID_API_KEY_HERE
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Add email provider configuration
email.provider=SENDGRID
email.sendgrid.api-key=YOUR_SENDGRID_API_KEY_HERE
email.sendgrid.from-email=noreply@yourdomain.com
email.sendgrid.from-name=Attendance Management System
```

### 3. Disable Email Temporarily (For Testing)

If you want to test your app without email functionality:

```properties
# Disable email sending
email.provider=DISABLED
email.fallback-mode=log
```

This will log email content instead of sending it.

### 4. Test Your Configuration

Add this to your application.properties to see detailed email logs:

```properties
# Enable detailed email logging
logging.level.org.springframework.mail=DEBUG
logging.level.com.example.attendancemanagement.service.EmailService=DEBUG
```

## Environment Variables for Hosting

### Heroku

```bash
heroku config:set SENDGRID_API_KEY=your_sendgrid_api_key
heroku config:set SPRING_PROFILES_ACTIVE=production
```

### Railway

Set these in Railway dashboard:

- `SENDGRID_API_KEY` = your_sendgrid_api_key
- `SPRING_PROFILES_ACTIVE` = production

### DigitalOcean App Platform

Set these in App Platform dashboard:

- `SENDGRID_API_KEY` = your_sendgrid_api_key
- `SPRING_PROFILES_ACTIVE` = production

## Testing Commands

### Test SMTP Connection

```bash
# Test if port 587 is accessible
telnet smtp.gmail.com 587

# Test if port 25 is accessible
telnet smtp.gmail.com 25
```

### Test from Hosting Environment

```bash
# SSH into your hosting environment and test
curl -v telnet://smtp.gmail.com:587
```

## Common Error Messages and Solutions

### "Connection refused" or "Connection timeout"

- **Cause**: Hosting platform blocks SMTP ports
- **Solution**: Use SendGrid or try port 25

### "535 Authentication Failed"

- **Cause**: Wrong username/password or 2FA not enabled
- **Solution**: Use App Password for Gmail or switch to SendGrid

### "SSL/TLS errors"

- **Cause**: SSL configuration mismatch
- **Solution**: Try disabling SSL temporarily or use SendGrid

## Recommended Next Steps

1. **Try port 25** first (quickest test)
2. **Set up SendGrid** (most reliable for hosting)
3. **Test locally** before deploying
4. **Check hosting platform documentation** for email restrictions

## Support

If you're still having issues:

1. Check your hosting platform's documentation for email restrictions
2. Try different email providers (Mailgun, Amazon SES)
3. Contact your hosting provider's support
4. Use the fallback logging mode to debug email content
