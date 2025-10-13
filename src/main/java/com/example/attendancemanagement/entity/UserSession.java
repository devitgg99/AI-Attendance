package com.example.attendancemanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "user_session")
@Data
@EqualsAndHashCode(callSuper = true)
public class UserSession extends BaseEntity {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "session_id", updatable = false, nullable = false)
    private UUID sessionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "user_id")
    private User user;
    
    @Column(name = "harsh_refresh_token", columnDefinition = "TEXT", nullable = false)
    private String harshRefreshToken;
    
    @Column(name = "fcm_token", columnDefinition = "TEXT")
    private String fcmToken;
    
    @Column(name = "device_id", length = 255)
    private String deviceId;
    
    @Column(name = "pin_harsh", columnDefinition = "TEXT")
    private String pinHarsh;
}



