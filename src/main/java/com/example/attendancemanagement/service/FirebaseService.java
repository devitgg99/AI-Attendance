package com.example.attendancemanagement.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FirebaseService {

    public void sendNotification(String fcmToken, String title, String body, Map<String, String> data) {
        if (fcmToken == null || fcmToken.isBlank()) return;
        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                .putAllData(data == null ? java.util.Collections.emptyMap() : data)
                .build();
        FirebaseMessaging.getInstance(FirebaseApp.getInstance()).sendAsync(message);
    }
}




