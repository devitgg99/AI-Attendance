package com.example.attendancemanagement.entity;

import com.example.attendancemanagement.enums.UserStatus;
import com.example.attendancemanagement.enums.UserStatusDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID userId;
    
    @JsonProperty("full_name")
    @Column(name = "full_name", length = 100, nullable = false)
    private String fullName;
    
    @Column(name = "email", length = 50, nullable = false, unique = true)
    private String email;
    
    @Column(name = "password", columnDefinition = "TEXT", nullable = false)
    private String password;
    
    @JsonProperty("user_status")
    @JsonDeserialize(using = UserStatusDeserializer.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    private UserStatus userStatus;
    
    @JsonProperty("user_info")
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "user_info", columnDefinition = "jsonb")
    private Map<String, Object> userInfo;
    
    // One-to-many relationship with UserSession
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserSession> userSessions;
}
