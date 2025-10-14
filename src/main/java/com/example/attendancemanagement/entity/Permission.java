package com.example.attendancemanagement.entity;

import com.example.attendancemanagement.enums.PermissionCategory;
import com.example.attendancemanagement.enums.PermissionStatus;
import com.example.attendancemanagement.enums.Shift;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "permissions")
@Data
@EqualsAndHashCode(callSuper = true)
public class Permission extends BaseEntity {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "permission_id", updatable = false, nullable = false)
    private UUID permissionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private PermissionCategory category;
    
    @JsonProperty("request_date")
    @Column(name = "request_date", nullable = false)
    private LocalDate requestDate;
    
    @JsonProperty("start_time")
    @Column(name = "start_time")
    private LocalTime startTime;
    
    @JsonProperty("end_time")
    @Column(name = "end_time")
    private LocalTime endTime;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "shift")
    private Shift shift;
    
    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PermissionStatus status;
}
