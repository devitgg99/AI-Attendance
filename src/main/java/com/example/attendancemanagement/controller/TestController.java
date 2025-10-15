package com.example.attendancemanagement.controller;

import com.example.attendancemanagement.service.FirebaseService;
import com.example.attendancemanagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    private final FirebaseService firebaseService;
    private final NotificationService notificationService;

    /**
     * Test Firebase connection
     */
    @GetMapping("/firebase/connection")
    public ResponseEntity<Map<String, Object>> testFirebaseConnection() {
        boolean isConnected = firebaseService.testFCMConnection();
        
        Map<String, Object> response = Map.of(
            "connected", isConnected,
            "timestamp", System.currentTimeMillis(),
            "message", isConnected ? "Firebase connection successful" : "Firebase connection failed"
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Send test notification to specific FCM token
     */
    @PostMapping("/firebase/send")
    public ResponseEntity<Map<String, Object>> sendTestNotification(@RequestBody Map<String, String> request) {
        String fcmToken = request.get("fcmToken");
        String title = request.getOrDefault("title", "Test Notification");
        String body = request.getOrDefault("body", "This is a test notification from your backend");
        
        log.info("Sending test notification - Title: {}, Body: {}", title, body);
        
        if (fcmToken == null || fcmToken.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "FCM token is required"
            ));
        }
        
        try {
            firebaseService.sendTestNotification(fcmToken);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Test notification sent successfully",
                "fcmToken", fcmToken.substring(0, Math.min(8, fcmToken.length())) + "...",
                "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            log.error("Failed to send test notification: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }

    /**
     * Send test notification to user by user ID
     */
    @PostMapping("/firebase/send-to-user/{userId}")
    public ResponseEntity<Map<String, Object>> sendTestNotificationToUser(
            @PathVariable UUID userId,
            @RequestBody(required = false) Map<String, String> request) {
        
        String title = "Test Notification";
        String body = "This is a test notification from your backend";
        
        if (request != null) {
            title = request.getOrDefault("title", title);
            body = request.getOrDefault("body", body);
        }
        
        try {
            Map<String, String> testData = Map.of(
                "type", "test",
                "userId", userId.toString(),
                "timestamp", String.valueOf(System.currentTimeMillis())
            );
            
            notificationService.sendNotificationToUser(userId, title, body, testData);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Test notification sent to user successfully",
                "userId", userId.toString(),
                "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            log.error("Failed to send test notification to user {}: {}", userId, e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", e.getMessage(),
                "userId", userId.toString(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }

    /**
     * Get FCM token information for debugging
     */
    @PostMapping("/firebase/validate-token")
    public ResponseEntity<Map<String, Object>> validateFCMToken(@RequestBody Map<String, String> request) {
        String fcmToken = request.get("fcmToken");
        
        if (fcmToken == null || fcmToken.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "valid", false,
                "error", "FCM token is required"
            ));
        }
        
        // Basic FCM token validation
        boolean isValidFormat = fcmToken.length() > 100 && fcmToken.matches("[A-Za-z0-9:_-]+");
        
        Map<String, Object> response = Map.of(
            "valid", isValidFormat,
            "length", fcmToken.length(),
            "prefix", fcmToken.substring(0, Math.min(10, fcmToken.length())),
            "suffix", fcmToken.substring(Math.max(0, fcmToken.length() - 10)),
            "timestamp", System.currentTimeMillis()
        );
        
        return ResponseEntity.ok(response);
    }
}
