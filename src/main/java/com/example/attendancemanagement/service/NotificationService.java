package com.example.attendancemanagement.service;

import com.example.attendancemanagement.entity.User;
import com.example.attendancemanagement.entity.UserSession;
import com.example.attendancemanagement.enums.UserStatus;
import com.example.attendancemanagement.repository.UserRepository;
import com.example.attendancemanagement.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final FirebaseService firebaseService;
    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    
    private static final ZoneId JAKARTA_ZONE = ZoneId.of("Asia/Jakarta");

    /**
     * Send check-in reminder notification at 7:58 AM
     */
    @Scheduled(cron = "0 58 7 * * MON-FRI")
    public void sendCheckInReminder() {
        log.info("Sending check-in reminder notifications");
        
        ZonedDateTime jakartaNow = ZonedDateTime.now(JAKARTA_ZONE);
        LocalDate today = jakartaNow.toLocalDate();
        
        // Get all active users
        List<User> activeUsers = userRepository.findAll().stream()
            .filter(user -> user.getUserStatus() == UserStatus.ACTIVE)
            .toList();
        
        for (User user : activeUsers) {
            try {
                // Get user's session with FCM token
                UserSession activeSession = userSessionRepository.findByUserUserId(user.getUserId())
                    .stream()
                    .filter(session -> session.getFcmToken() != null && !session.getFcmToken().isBlank())
                    .findFirst()
                    .orElse(null);
                
                if (activeSession != null) {
                    Map<String, String> data = new HashMap<>();
                    data.put("type", "checkin_reminder");
                    data.put("date", today.toString());
                    data.put("time", "08:00");
                    
                    firebaseService.sendNotification(
                        activeSession.getFcmToken(),
                        "Check-in Reminder",
                        "Don't forget to check in! Your check-in time is at 8:00 AM.",
                        data
                    );
                    
                    log.info("Sent check-in reminder to user: {}", user.getEmail());
                }
            } catch (Exception e) {
                log.error("Failed to send check-in reminder to user {}: {}", user.getEmail(), e.getMessage());
            }
        }
    }

    /**
     * Send check-out reminder notification at 4:58 PM
     */
    @Scheduled(cron = "0 58 16 * * MON-FRI")
    public void sendCheckOutReminder() {
        log.info("Sending check-out reminder notifications");
        
        ZonedDateTime jakartaNow = ZonedDateTime.now(JAKARTA_ZONE);
        LocalDate today = jakartaNow.toLocalDate();
        
        // Get all active users
        List<User> activeUsers = userRepository.findAll().stream()
            .filter(user -> user.getUserStatus() == UserStatus.ACTIVE)
            .toList();
        
        for (User user : activeUsers) {
            try {
                // Get user's session with FCM token
                UserSession activeSession = userSessionRepository.findByUserUserId(user.getUserId())
                    .stream()
                    .filter(session -> session.getFcmToken() != null && !session.getFcmToken().isBlank())
                    .findFirst()
                    .orElse(null);
                
                if (activeSession != null) {
                    Map<String, String> data = new HashMap<>();
                    data.put("type", "checkout_reminder");
                    data.put("date", today.toString());
                    data.put("time", "17:00");
                    
                    firebaseService.sendNotification(
                        activeSession.getFcmToken(),
                        "Check-out Reminder",
                        "Don't forget to check out! Your check-out time is at 5:00 PM.",
                        data
                    );
                    
                    log.info("Sent check-out reminder to user: {}", user.getEmail());
                }
            } catch (Exception e) {
                log.error("Failed to send check-out reminder to user {}: {}", user.getEmail(), e.getMessage());
            }
        }
    }

    /**
     * Send missed checkout notification at 6:00 PM
     */
    @Scheduled(cron = "0 0 18 * * MON-FRI")
    public void sendMissedCheckoutNotification() {
        log.info("Sending missed checkout notifications");
        
        ZonedDateTime jakartaNow = ZonedDateTime.now(JAKARTA_ZONE);
        LocalDate today = jakartaNow.toLocalDate();
        
        // Get all active users
        List<User> activeUsers = userRepository.findAll().stream()
            .filter(user -> user.getUserStatus() == UserStatus.ACTIVE)
            .toList();
        
        for (User user : activeUsers) {
            try {
                // Get user's session with FCM token
                UserSession activeSession = userSessionRepository.findByUserUserId(user.getUserId())
                    .stream()
                    .filter(session -> session.getFcmToken() != null && !session.getFcmToken().isBlank())
                    .findFirst()
                    .orElse(null);
                
                if (activeSession != null) {
                    Map<String, String> data = new HashMap<>();
                    data.put("type", "missed_checkout");
                    data.put("date", today.toString());
                    data.put("time", "18:00");
                    
                    firebaseService.sendNotification(
                        activeSession.getFcmToken(),
                        "Missed Check-out",
                        "You haven't checked out yet! Please check out as soon as possible.",
                        data
                    );
                    
                    log.info("Sent missed checkout notification to user: {}", user.getEmail());
                }
            } catch (Exception e) {
                log.error("Failed to send missed checkout notification to user {}: {}", user.getEmail(), e.getMessage());
            }
        }
    }

    /**
     * Send custom notification to specific user
     */
    public void sendNotificationToUser(UUID userId, String title, String body, Map<String, String> data) {
        try {
            UserSession activeSession = userSessionRepository.findByUserUserId(userId)
                .stream()
                .filter(session -> session.getFcmToken() != null && !session.getFcmToken().isBlank())
                .findFirst()
                .orElse(null);
            
            if (activeSession != null) {
                firebaseService.sendNotification(activeSession.getFcmToken(), title, body, data);
                log.info("Sent custom notification to user: {}", userId);
            } else {
                log.warn("No active session with FCM token found for user: {}", userId);
            }
        } catch (Exception e) {
            log.error("Failed to send notification to user {}: {}", userId, e.getMessage());
        }
    }
}
