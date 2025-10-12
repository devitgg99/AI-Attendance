package com.example.attendancemanagement.repository;

import com.example.attendancemanagement.entity.Attendance;
import com.example.attendancemanagement.enums.CheckInStatus;
import com.example.attendancemanagement.enums.CheckOutStatus;
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
     * Find attendance record by user and check-in date
     */
    @Query("SELECT a FROM Attendance a WHERE a.user.userId = :userId AND DATE(a.checkIn) = :date")
    Optional<Attendance> findByUserUserIdAndCheckInDate(UUID userId, LocalDate date);
    
    /**
     * Find all attendance records for a user
     */
    List<Attendance> findByUserUserIdOrderByCheckInDesc(UUID userId);
    
    /**
     * Find attendance records for a user within date range
     */
    @Query("SELECT a FROM Attendance a WHERE a.user.userId = :userId " +
           "AND DATE(a.checkIn) BETWEEN :startDate AND :endDate " +
           "ORDER BY a.checkIn DESC")
    List<Attendance> findByUserUserIdAndCheckInDateBetweenOrderByCheckInDesc(
        UUID userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Find attendance records by check-in status
     */
    List<Attendance> findByCheckinStatusOrderByCheckInDesc(CheckInStatus checkinStatus);
    
    /**
     * Find attendance records by check-out status
     */
    List<Attendance> findByCheckoutStatusOrderByCheckoutOutDesc(CheckOutStatus checkoutStatus);
    
    /**
     * Find attendance records for a specific date
     */
    @Query("SELECT a FROM Attendance a WHERE DATE(a.checkIn) = :date ORDER BY a.checkIn DESC")
    List<Attendance> findByCheckInDateOrderByCheckInDesc(LocalDate date);
    
    /**
     * Check if attendance record exists for user and date
     */
    @Query("SELECT COUNT(a) > 0 FROM Attendance a WHERE a.user.userId = :userId AND DATE(a.checkIn) = :date")
    boolean existsByUserUserIdAndCheckInDate(UUID userId, LocalDate date);
    
    /**
     * Count attendance records for a user within date range
     */
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.user.userId = :userId " +
           "AND DATE(a.checkIn) BETWEEN :startDate AND :endDate")
    long countByUserUserIdAndCheckInDateBetween(UUID userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Find attendance records by user and check-in status
     */
    List<Attendance> findByUserUserIdAndCheckinStatusOrderByCheckInDesc(UUID userId, CheckInStatus checkinStatus);
    
    /**
     * Find attendance records by user and check-out status
     */
    List<Attendance> findByUserUserIdAndCheckoutStatusOrderByCheckoutOutDesc(UUID userId, CheckOutStatus checkoutStatus);
    
    /**
     * Custom query to find attendance records with specific criteria
     */
    @Query("SELECT a FROM Attendance a WHERE a.user.userId = :userId " +
           "AND DATE(a.checkIn) BETWEEN :startDate AND :endDate " +
           "AND a.checkinStatus = :checkinStatus " +
           "ORDER BY a.checkIn DESC")
    List<Attendance> findAttendanceByUserAndDateRangeAndCheckinStatus(
        @Param("userId") UUID userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("checkinStatus") CheckInStatus checkinStatus
    );
}