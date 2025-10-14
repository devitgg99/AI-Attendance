package com.example.attendancemanagement.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class FirebaseService {

    public void sendNotification(String fcmToken, String title, String body, Map<String, String> data) {
        if (fcmToken == null || fcmToken.isBlank()) return;

        // Android notification options (sound, high priority)
        AndroidConfig androidConfig = AndroidConfig.builder()
                .setPriority(AndroidConfig.Priority.HIGH)
                .setNotification(AndroidNotification.builder()
                        .setSound("default")
                        .build())
                .build();

        // iOS/APNs options (sound, alert, high priority)
        ApnsConfig apnsConfig = ApnsConfig.builder()
                .putHeader("apns-priority", "10")
                .setAps(Aps.builder()
                        .setSound("default")
                        .setContentAvailable(false)
                        .build())
                .build();

        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                .setAndroidConfig(androidConfig)
                .setApnsConfig(apnsConfig)
                .putAllData(data == null ? java.util.Collections.emptyMap() : data)
                .build();

        try {
            String messageId = FirebaseMessaging.getInstance(FirebaseApp.getInstance()).send(message);
            log.info("FCM sent successfully. messageId={}, tokenHash={}", messageId, Integer.toHexString(fcmToken.hashCode()));
        } catch (Exception e) {
            log.error("FCM send failed: {} tokenHash={}", e.getMessage(), Integer.toHexString(fcmToken.hashCode()));
            
            // Provide specific guidance for common errors
            if (e.getMessage().contains("SenderId mismatch")) {
                log.error("SenderId mismatch detected. Please verify:");
                log.error("1. iOS app's GoogleService-Info.plist matches backend Firebase project");
                log.error("2. Project number in iOS app: {}", "attendance-notification-87f9d");
                log.error("3. Regenerate FCM token after fixing configuration");
            }
        }
    }
}





