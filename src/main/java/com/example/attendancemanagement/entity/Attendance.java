package com.example.attendancemanagement.entity;

import com.example.attendancemanagement.enums.CheckInStatus;
import com.example.attendancemanagement.enums.CheckOutStatus;
import com.example.attendancemanagement.enums.DateStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "attendance")
@Data
@EqualsAndHashCode(callSuper = true)
public class Attendance extends BaseEntity {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "attendance_id", updatable = false, nullable = false)
    private UUID attendanceId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "user_id")
    private User user;
    
    @JsonProperty("check_in")
    @Column(name = "check_in", columnDefinition = "TIME")
    private LocalTime checkIn;
    
    @JsonProperty("checkin_status")
    @Enumerated(EnumType.STRING)
    @Column(name = "checkin_status")
    private CheckInStatus checkinStatus;
    
    @JsonProperty("checkout_out")
    @Column(name = "checkout_out", columnDefinition = "TIME")
    private LocalTime checkoutOut;
    
    @JsonProperty("checkout_status")
    @Enumerated(EnumType.STRING)
    @Column(name = "checkout_status")
    private CheckOutStatus checkoutStatus;

    @JsonProperty("date_status")
    @Enumerated(EnumType.STRING)
    @Column(name = "date_status")
    private DateStatus dateStatus;
}