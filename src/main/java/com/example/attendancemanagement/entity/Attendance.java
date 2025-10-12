package com.example.attendancemanagement.entity;

import com.example.attendancemanagement.enums.AttendanceStatus;
import com.example.attendancemanagement.enums.CheckInStatus;
import com.example.attendancemanagement.enums.CheckOutStatus;
import com.example.attendancemanagement.enums.DateStatus;
import com.example.attendancemanagement.enums.Shift;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    
    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;
    
    @JsonProperty("check_in_time")
    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;
    
    @JsonProperty("check_out_time")
    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;
    
    @JsonProperty("attendance_status")
    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_status", nullable = false)
    private AttendanceStatus attendanceStatus;
    
    @JsonProperty("check_in_status")
    @Enumerated(EnumType.STRING)
    @Column(name = "check_in_status")
    private CheckInStatus checkInStatus;
    
    @JsonProperty("check_out_status")
    @Enumerated(EnumType.STRING)
    @Column(name = "check_out_status")
    private CheckOutStatus checkOutStatus;
    
    @JsonProperty("shift")
    @Enumerated(EnumType.STRING)
    @Column(name = "shift", nullable = false)
    private Shift shift;
    
    @JsonProperty("date_status")
    @Enumerated(EnumType.STRING)
    @Column(name = "date_status", nullable = false)
    private DateStatus dateStatus;

}
