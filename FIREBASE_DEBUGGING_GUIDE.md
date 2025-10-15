# Firebase Notification Debugging Guide

## Problem Analysis

Your Firebase notifications work in Firebase Console but not through your API. This indicates:

1. ✅ Firebase project configuration is correct
2. ✅ Client app can receive notifications
3. ❌ Backend API has issues sending notifications

## Root Cause Found

**CRITICAL BUG FIXED**: Your `FirebaseService.java` had an incomplete line that was missing the actual Firebase messaging call. This has been fixed.

## Debugging Steps

### 1. Test Firebase Connection

Use the new test endpoint:

```bash
GET /api/test/firebase/connection
```

Expected response:

```json
{
  "connected": true,
  "timestamp": 1234567890,
  "message": "Firebase connection successful"
}
```

### 2. Test FCM Token Validation

```bash
POST /api/test/firebase/validate-token
Content-Type: application/json

{
  "fcmToken": "your_fcm_token_here"
}
```

### 3. Send Test Notification

```bash
POST /api/test/firebase/send
Content-Type: application/json

{
  "fcmToken": "your_fcm_token_here",
  "title": "Test Title",
  "body": "Test Body"
}
```

### 4. Send Test Notification to User

```bash
POST /api/test/firebase/send-to-user/{userId}
Content-Type: application/json

{
  "title": "Test Title",
  "body": "Test Body"
}
```

## Common Issues and Solutions

### 1. FCM Token Issues

**Problem**: Token is invalid or expired
**Solution**:

- Check token format (should be 100+ characters)
- Regenerate token from client app
- Verify token is properly stored in database

### 2. Firebase Configuration Issues

**Problem**: SenderId mismatch
**Solution**:

- Verify `google-services.json` matches your Firebase project
- Check project ID: `attendance-notification-87f9d`
- Regenerate FCM token after fixing configuration

### 3. Network/Firewall Issues

**Problem**: Cannot reach Firebase servers
**Solution**:

- Check if hosting platform blocks Firebase endpoints
- Verify internet connectivity
- Check firewall settings

### 4. Authentication Issues

**Problem**: Firebase service account not properly configured
**Solution**:

- Verify `firebase-service-account.json` is correct
- Check file permissions
- Ensure service account has FCM permissions

## Enhanced Logging

The updated `FirebaseService` now provides detailed logging:

```
INFO  - Attempting to send FCM notification to token: ABCDEFGH...XYZ12345
INFO  - FCM sent successfully. messageId=projects/attendance-notification-87f9d/messages/0:1234567890, tokenHash=abc123
```

Or error details:

```
ERROR - FCM send failed with FirebaseMessagingException: UNREGISTERED tokenHash=abc123
ERROR - FCM token is unregistered - user may have uninstalled app or token expired
ERROR - Recommendation: Remove this token from database and request new token from client
```

## Testing Checklist

### ✅ Backend Testing

- [ ] Firebase connection test passes
- [ ] FCM token validation passes
- [ ] Test notification endpoint works
- [ ] User notification endpoint works

### ✅ Client Testing

- [ ] FCM token is generated correctly
- [ ] Token is sent to backend during login
- [ ] Token is stored in database
- [ ] Client can receive notifications from Firebase Console

### ✅ Integration Testing

- [ ] Login triggers notification
- [ ] Scheduled notifications work
- [ ] Custom notifications work

## Quick Fixes to Try

### 1. Restart Your Application

```bash
# Stop and restart your Spring Boot application
./gradlew bootRun
```

### 2. Check Logs

Look for these log messages:

```
INFO  - FCM sent successfully. messageId=...
ERROR - FCM send failed: ...
```

### 3. Test with Known Good Token

Use a token that works in Firebase Console:

```bash
curl -X POST http://localhost:8080/api/test/firebase/send \
  -H "Content-Type: application/json" \
  -d '{"fcmToken": "your_working_token_here"}'
```

### 4. Verify Database

Check if FCM tokens are properly stored:

```sql
SELECT user_id, fcm_token, created_at
FROM user_session
WHERE fcm_token IS NOT NULL;
```

## Environment-Specific Issues

### Development Environment

- Ensure `firebase-service-account.json` is in `src/main/resources/`
- Check file permissions
- Verify project ID matches

### Production Environment

- Ensure Firebase service account file is deployed
- Check environment variables
- Verify network connectivity to Firebase

## Next Steps

1. **Test the fixed code** using the new test endpoints
2. **Check application logs** for detailed error messages
3. **Verify FCM tokens** are properly stored and valid
4. **Test with a known working token** from Firebase Console

## Support

If issues persist:

1. Check application logs for specific error messages
2. Use the test endpoints to isolate the problem
3. Verify Firebase project configuration
4. Test with different FCM tokens
