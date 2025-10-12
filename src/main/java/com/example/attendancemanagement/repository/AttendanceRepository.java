package com.example.attendancemanagement.repository;

import com.example.attendancemanagement.entity.Attendance;
import com.example.attendancemanagement.enums.AttendanceStatus;
import com.example.attendancemanagement.enums.DateStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {
    
    /**
     * Find attendance record by user and date
     */
    Optional<Attendance> findByUserUserIdAndAttendanceDate(UUID userId, LocalDate attendanceDate);
    
    /**
     * Find all attendance records for a user
     */
    List<Attendance> findByUserUserIdOrderByAttendanceDateDesc(UUID userId);
    
    /**
     * Find attendance records for a user within date range
     */
    List<Attendance> findByUserUserIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
        UUID userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Find attendance records by status
     */
    List<Attendance> findByAttendanceStatusOrderByAttendanceDateDesc(AttendanceStatus status);
    
    /**
     * Find attendance records by date status
     */
    List<Attendance> findByDateStatusOrderByAttendanceDateDesc(DateStatus dateStatus);
    
    /**
     * Find attendance records for a specific date
     */
    List<Attendance> findByAttendanceDateOrderByCreatedAtDesc(LocalDate attendanceDate);
    
    /**
     * Check if attendance record exists for user and date
     */
    boolean existsByUserUserIdAndAttendanceDate(UUID userId, LocalDate attendanceDate);
    
    /**
     * Count attendance records for a user within date range
     */
    long countByUserUserIdAndAttendanceDateBetween(UUID userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Find attendance records by user and status
     */
    List<Attendance> findByUserUserIdAndAttendanceStatusOrderByAttendanceDateDesc(UUID userId, AttendanceStatus status);
    
    /**
     * Custom query to find attendance records with specific criteria
     */
    @Query("SELECT a FROM Attendance a WHERE a.user.userId = :userId " +
           "AND a.attendanceDate BETWEEN :startDate AND :endDate " +
           "AND a.attendanceStatus = :status " +
           "ORDER BY a.attendanceDate DESC")
    List<Attendance> findAttendanceByUserAndDateRangeAndStatus(
        @Param("userId") UUID userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("status") AttendanceStatus status
    );
}
