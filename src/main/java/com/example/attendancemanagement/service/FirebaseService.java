package com.example.attendancemanagement.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class FirebaseService {

    public void sendNotification(String fcmToken, String title, String body, Map<String, String> data) {
        if (fcmToken == null || fcmToken.isBlank()) {
            log.warn("FCM token is null or blank, skipping notification");
            return;
        }

        log.info("Attempting to send FCM notification to token: {}...{}", 
                fcmToken.substring(0, Math.min(8, fcmToken.length())), 
                fcmToken.substring(Math.max(0, fcmToken.length() - 8)));

        // Android notification options (sound, high priority)
        AndroidConfig androidConfig = AndroidConfig.builder()
                .setPriority(AndroidConfig.Priority.HIGH)
                .setNotification(AndroidNotification.builder()
                        .setSound("default")
                        .setClickAction("FLUTTER_NOTIFICATION_CLICK")
                        .build())
                .build();

        // iOS/APNs options (sound, alert, high priority)
        ApnsConfig apnsConfig = ApnsConfig.builder()
                .putHeader("apns-priority", "10")
                .setAps(Aps.builder()
                        .setSound("default")
                        .setContentAvailable(false)
                        .setAlert(title + ": " + body)
                        .build())
                .build();

        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .setAndroidConfig(androidConfig)
                .setApnsConfig(apnsConfig)
                .putAllData(data == null ? java.util.Collections.emptyMap() : data)
                .build();

        try {
            String messageId = FirebaseMessaging.getInstance(FirebaseApp.getInstance()).send(message);
            log.info("FCM sent successfully. messageId={}, tokenHash={}", messageId, Integer.toHexString(fcmToken.hashCode()));
        } catch (FirebaseMessagingException e) {
            log.error("FCM send failed with FirebaseMessagingException: {} tokenHash={}", e.getMessage(), Integer.toHexString(fcmToken.hashCode()));
            handleFirebaseMessagingException(e, fcmToken);
        } catch (Exception e) {
            log.error("FCM send failed with general exception: {} tokenHash={}", e.getMessage(), Integer.toHexString(fcmToken.hashCode()));
            log.error("Exception type: {}", e.getClass().getSimpleName());
            e.printStackTrace();
        }
    }

    /**
     * Handle specific Firebase messaging exceptions with detailed guidance
     */
    private void handleFirebaseMessagingException(FirebaseMessagingException e, String fcmToken) {
        String errorCode = e.getErrorCode().toString();
        log.error("Firebase error code: {}", errorCode);
        
        switch (errorCode) {
            case "INVALID_ARGUMENT":
                log.error("Invalid argument error - check FCM token format");
                log.error("FCM token length: {}", fcmToken.length());
                break;
            case "UNREGISTERED":
                log.error("FCM token is unregistered - user may have uninstalled app or token expired");
                log.error("Recommendation: Remove this token from database and request new token from client");
                break;
            case "SENDER_ID_MISMATCH":
                log.error("SenderId mismatch detected. Please verify:");
                log.error("1. Client app's google-services.json/google-services.plist matches backend Firebase project");
                log.error("2. Project ID in client app: attendance-notification-87f9d");
                log.error("3. Regenerate FCM token after fixing configuration");
                break;
            case "QUOTA_EXCEEDED":
                log.error("FCM quota exceeded - check Firebase console for usage limits");
                break;
            case "UNAVAILABLE":
                log.error("FCM service unavailable - temporary issue, retry later");
                break;
            case "INTERNAL":
                log.error("Internal FCM error - check Firebase project configuration");
                break;
            default:
                log.error("Unknown Firebase error: {}", errorCode);
        }
    }

    /**
     * Test FCM connection and configuration
     */
    public boolean testFCMConnection() {
        try {
            FirebaseApp app = FirebaseApp.getInstance();
            log.info("Firebase app initialized: {}", app.getName());
            log.info("Firebase project ID: {}", app.getOptions().getProjectId());
            return true;
        } catch (Exception e) {
            log.error("Firebase connection test failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Send test notification to verify FCM setup
     */
    public void sendTestNotification(String fcmToken) {
        Map<String, String> testData = Map.of(
            "type", "test",
            "timestamp", String.valueOf(System.currentTimeMillis())
        );
        
        sendNotification(fcmToken, "Test Notification", "This is a test notification from your backend", testData);
    }
}





