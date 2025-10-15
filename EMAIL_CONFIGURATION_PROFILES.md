# Email Configuration Profiles

## application-local.properties (Development)

```properties
# Local development with Gmail SMTP
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=mengse.thun13@gmail.com
spring.mail.password=qdrw ktbz tebz hqon
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.ssl.trust=smtp.gmail.com

# Email provider configuration
email.provider=SMTP
email.fallback-mode=log
```

## application-production.properties (Production with SendGrid)

```properties
# Production with SendGrid API (recommended for hosting)
spring.mail.host=smtp.sendgrid.net
spring.mail.port=587
spring.mail.username=apikey
spring.mail.password=${SENDGRID_API_KEY}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Email provider configuration
email.provider=SENDGRID
email.fallback-mode=log
email.sendgrid.api-key=${SENDGRID_API_KEY}
email.sendgrid.from-email=noreply@yourdomain.com
email.sendgrid.from-name=Attendance Management System
```

## application-production.properties (Production with Mailgun)

```properties
# Production with Mailgun API
spring.mail.host=smtp.mailgun.org
spring.mail.port=587
spring.mail.username=postmaster@${MAILGUN_DOMAIN}
spring.mail.password=${MAILGUN_API_KEY}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Email provider configuration
email.provider=MAILGUN
email.fallback-mode=log
email.mailgun.api-key=${MAILGUN_API_KEY}
email.mailgun.domain=${MAILGUN_DOMAIN}
email.mailgun.from-email=noreply@${MAILGUN_DOMAIN}
email.mailgun.from-name=Attendance Management System
```

## application-production.properties (Production with Alternative SMTP)

```properties
# Try different SMTP ports if 587 is blocked
spring.mail.host=smtp.gmail.com
spring.mail.port=25
spring.mail.username=mengse.thun13@gmail.com
spring.mail.password=qdrw ktbz tebz hqon
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=false
spring.mail.properties.mail.smtp.ssl.enable=false

# Email provider configuration
email.provider=SMTP
email.fallback-mode=log
```

## Environment Variables for Hosting Platforms

### Heroku

```bash
# Set these in Heroku dashboard or CLI
heroku config:set SENDGRID_API_KEY=your_sendgrid_api_key
heroku config:set SPRING_PROFILES_ACTIVE=production
```

### Railway

```bash
# Set these in Railway dashboard
SENDGRID_API_KEY=your_sendgrid_api_key
SPRING_PROFILES_ACTIVE=production
```

### DigitalOcean App Platform

```bash
# Set these in App Platform dashboard
SENDGRID_API_KEY=your_sendgrid_api_key
SPRING_PROFILES_ACTIVE=production
```

### AWS Elastic Beanstalk

```bash
# Set these in EB environment variables
SENDGRID_API_KEY=your_sendgrid_api_key
SPRING_PROFILES_ACTIVE=production
```

## Quick Fixes for Current Issue

### Option 1: Try Different SMTP Port

Update your current `application.properties`:

```properties
# Change from port 587 to 25
spring.mail.port=25
spring.mail.properties.mail.smtp.starttls.enable=false
spring.mail.properties.mail.smtp.ssl.enable=false
```

### Option 2: Use SendGrid (Recommended)

1. Sign up for SendGrid free account
2. Get API key from SendGrid dashboard
3. Update your `application.properties`:

```properties
# Replace SMTP configuration with SendGrid
spring.mail.host=smtp.sendgrid.net
spring.mail.port=587
spring.mail.username=apikey
spring.mail.password=YOUR_SENDGRID_API_KEY
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Add email provider configuration
email.provider=SENDGRID
email.sendgrid.api-key=YOUR_SENDGRID_API_KEY
email.sendgrid.from-email=noreply@yourdomain.com
email.sendgrid.from-name=Attendance Management System
```

### Option 3: Disable Email Temporarily

```properties
# Disable email sending for testing
email.provider=DISABLED
email.fallback-mode=log
```
