package com.example.attendancemanagement.entity;

import com.example.attendancemanagement.enums.OvertimeStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Entity representing overtime requests
 * One user can have zero or many overtime requests
 */
@Entity
@Table(name = "overtime")
@Data
@EqualsAndHashCode(callSuper = true)
public class Overtime extends BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "overtime_id", updatable = false, nullable = false)
    private UUID overtimeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "request_date", nullable = false)
    private LocalDate requestDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "objective", columnDefinition = "TEXT")
    private String objective;

    @Column(name = "duration", precision = 5, scale = 2, nullable = false)
    private BigDecimal duration;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OvertimeStatus status;

    @Column(name = "is_weekday", nullable = false)
    private Boolean isWeekday;

    // Helper method to calculate duration
    public void calculateDuration() {
        if (startTime != null && endTime != null) {
            long minutes = java.time.Duration.between(startTime, endTime).toMinutes();
            this.duration = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, java.math.RoundingMode.HALF_UP);
        }
    }

    // Helper method to determine if it's a weekday
    public void determineIsWeekday() {
        if (requestDate != null) {
            java.time.DayOfWeek dayOfWeek = requestDate.getDayOfWeek();
            this.isWeekday = dayOfWeek != java.time.DayOfWeek.SATURDAY && dayOfWeek != java.time.DayOfWeek.SUNDAY;
        }
    }

    // Method to set calculated fields - call this before saving
    public void setCalculatedFields() {
        calculateDuration();
        determineIsWeekday();
    }
}
