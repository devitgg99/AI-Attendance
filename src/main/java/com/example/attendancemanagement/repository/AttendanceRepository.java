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
     * Find attendance record by user and attendance date
     */
    @Query("SELECT a FROM Attendance a WHERE a.user.userId = :userId AND DATE(a.createdAt) = :date")
    Optional<Attendance> findByUserUserIdAndAttendanceDate(UUID userId, LocalDate date);
    
    /**
     * Find all attendance records for a user
     */
    List<Attendance> findByUserUserIdOrderByCheckInDesc(UUID userId);
    
    /**
     * Find attendance records for a user within date range
     */
    @Query("SELECT a FROM Attendance a WHERE a.user.userId = :userId " +
           "AND DATE(a.createdAt) BETWEEN :startDate AND :endDate " +
           "ORDER BY a.createdAt DESC")
    List<Attendance> findByUserUserIdAndAttendanceDateBetweenOrderByCreatedAtDesc(
        UUID userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Find attendance records for a user within date range (simplified method name)
     */
    @Query("SELECT a FROM Attendance a WHERE a.user.userId = :userId " +
           "AND DATE(a.createdAt) BETWEEN :startDate AND :endDate " +
           "ORDER BY a.createdAt DESC")
    List<Attendance> findByUserUserIdAndAttendanceDateBetween(
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
    @Query("SELECT a FROM Attendance a WHERE DATE(a.createdAt) = :date ORDER BY a.createdAt DESC")
    List<Attendance> findByAttendanceDateOrderByCreatedAtDesc(LocalDate date);
    
    /**
     * Check if attendance record exists for user and date
     */
    @Query("SELECT COUNT(a) > 0 FROM Attendance a WHERE a.user.userId = :userId AND DATE(a.createdAt) = :date")
    boolean existsByUserUserIdAndAttendanceDate(UUID userId, LocalDate date);
    
    /**
     * Count attendance records for a user within date range
     */
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.user.userId = :userId " +
           "AND DATE(a.createdAt) BETWEEN :startDate AND :endDate")
    long countByUserUserIdAndAttendanceDateBetween(UUID userId, LocalDate startDate, LocalDate endDate);
    
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
           "AND DATE(a.createdAt) BETWEEN :startDate AND :endDate " +
           "AND a.checkinStatus = :checkinStatus " +
           "ORDER BY a.createdAt DESC")
    List<Attendance> findAttendanceByUserAndDateRangeAndCheckinStatus(
        @Param("userId") UUID userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("checkinStatus") CheckInStatus checkinStatus
    );

    /**
     * Find all attendance records with late check-in status
     */
    @Query("SELECT a FROM Attendance a WHERE a.checkinStatus = 'CHECKIN_LATE' " +
           "AND DATE(a.createdAt) BETWEEN :startDate AND :endDate " +
           "ORDER BY a.createdAt DESC")
    List<Attendance> findLateCheckInRecords(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Find all attendance records with late check-in status for specific user
     */
    @Query("SELECT a FROM Attendance a WHERE a.user.userId = :userId " +
           "AND a.checkinStatus = 'CHECKIN_LATE' " +
           "AND DATE(a.createdAt) BETWEEN :startDate AND :endDate " +
           "ORDER BY a.createdAt DESC")
    List<Attendance> findLateCheckInRecordsByUser(
        @Param("userId") UUID userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Find all attendance records with specific date status
     */
    @Query("SELECT a FROM Attendance a WHERE a.dateStatus = :dateStatus " +
           "AND DATE(a.createdAt) BETWEEN :startDate AND :endDate " +
           "ORDER BY a.createdAt DESC")
    List<Attendance> findAttendanceByDateStatus(
        @Param("dateStatus") com.example.attendancemanagement.enums.DateStatus dateStatus,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Find all attendance records with late check-in and specific date status
     */
    @Query("SELECT a FROM Attendance a WHERE a.checkinStatus = 'CHECKIN_LATE' " +
           "AND a.dateStatus = :dateStatus " +
           "AND DATE(a.createdAt) BETWEEN :startDate AND :endDate " +
           "ORDER BY a.createdAt DESC")
    List<Attendance> findLateCheckInRecordsByDateStatus(
        @Param("dateStatus") com.example.attendancemanagement.enums.DateStatus dateStatus,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Count total attendance records in date range
     */
    @Query("SELECT COUNT(a) FROM Attendance a WHERE DATE(a.createdAt) BETWEEN :startDate AND :endDate")
    long countAttendanceRecordsInDateRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Count late check-in records in date range
     */
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.checkinStatus = 'CHECKIN_LATE' " +
           "AND DATE(a.createdAt) BETWEEN :startDate AND :endDate")
    long countLateCheckInRecordsInDateRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Find all attendance records in date range
     */
    @Query("SELECT a FROM Attendance a WHERE DATE(a.createdAt) BETWEEN :startDate AND :endDate " +
           "ORDER BY a.createdAt DESC")
    List<Attendance> findAllAttendanceRecordsInDateRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Find attendance records with missed check-in (no check-in record exists)
     * This finds users who should have checked in but didn't
     */
    @Query("SELECT a FROM Attendance a WHERE a.checkIn IS NULL " +
           "AND DATE(a.createdAt) BETWEEN :startDate AND :endDate " +
           "ORDER BY a.createdAt DESC")
    List<Attendance> findMissedCheckInRecords(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Find attendance records with missed check-in for specific date status
     */
    @Query("SELECT a FROM Attendance a WHERE a.checkIn IS NULL " +
           "AND a.dateStatus = :dateStatus " +
           "AND DATE(a.createdAt) BETWEEN :startDate AND :endDate " +
           "ORDER BY a.createdAt DESC")
    List<Attendance> findMissedCheckInRecordsByDateStatus(
        @Param("dateStatus") com.example.attendancemanagement.enums.DateStatus dateStatus,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Find attendance records with missed check-out (checked in but didn't check out)
     */
    @Query("SELECT a FROM Attendance a WHERE a.checkIn IS NOT NULL " +
           "AND a.checkoutOut IS NULL " +
           "AND DATE(a.createdAt) BETWEEN :startDate AND :endDate " +
           "ORDER BY a.createdAt DESC")
    List<Attendance> findMissedCheckOutRecords(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Find attendance records with missed check-out for specific date status
     */
    @Query("SELECT a FROM Attendance a WHERE a.checkIn IS NOT NULL " +
           "AND a.checkoutOut IS NULL " +
           "AND a.dateStatus = :dateStatus " +
           "AND DATE(a.createdAt) BETWEEN :startDate AND :endDate " +
           "ORDER BY a.createdAt DESC")
    List<Attendance> findMissedCheckOutRecordsByDateStatus(
        @Param("dateStatus") com.example.attendancemanagement.enums.DateStatus dateStatus,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Find present attendance records (checked in on time and completed checkout)
     */
    @Query("SELECT a FROM Attendance a WHERE a.checkIn IS NOT NULL " +
           "AND a.checkoutOut IS NOT NULL " +
           "AND a.checkinStatus = 'CHECKIN' " +
           "AND DATE(a.createdAt) BETWEEN :startDate AND :endDate " +
           "ORDER BY a.createdAt DESC")
    List<Attendance> findPresentRecords(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Find present attendance records for specific date status
     */
    @Query("SELECT a FROM Attendance a WHERE a.checkIn IS NOT NULL " +
           "AND a.checkoutOut IS NOT NULL " +
           "AND a.checkinStatus = 'CHECKIN' " +
           "AND a.dateStatus = :dateStatus " +
           "AND DATE(a.createdAt) BETWEEN :startDate AND :endDate " +
           "ORDER BY a.createdAt DESC")
    List<Attendance> findPresentRecordsByDateStatus(
        @Param("dateStatus") com.example.attendancemanagement.enums.DateStatus dateStatus,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Find absent records (users who didn't check in at all on work days)
     * This is a complex query that would need to compare expected work days
     * For now, we'll use missed check-in as a proxy
     */
    @Query("SELECT a FROM Attendance a WHERE a.checkIn IS NULL " +
           "AND a.dateStatus IN ('WEEKDAY', 'OVERTIME') " +
           "AND DATE(a.createdAt) BETWEEN :startDate AND :endDate " +
           "ORDER BY a.createdAt DESC")
    List<Attendance> findAbsentRecords(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Find absent records for specific date status
     */
    @Query("SELECT a FROM Attendance a WHERE a.checkIn IS NULL " +
           "AND a.dateStatus = :dateStatus " +
           "AND DATE(a.createdAt) BETWEEN :startDate AND :endDate " +
           "ORDER BY a.createdAt DESC")
    List<Attendance> findAbsentRecordsByDateStatus(
        @Param("dateStatus") com.example.attendancemanagement.enums.DateStatus dateStatus,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    // User-specific methods for attendance status filtering
    
    /**
     * Find missed check-in records for specific user
     */
    @Query("SELECT a FROM Attendance a WHERE a.user.userId = :userId " +
           "AND a.checkIn IS NULL " +
           "AND DATE(a.createdAt) BETWEEN :startDate AND :endDate " +
           "ORDER BY a.createdAt DESC")
    List<Attendance> findMissedCheckInRecordsByUser(
        @Param("userId") UUID userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Find missed check-out records for specific user
     */
    @Query("SELECT a FROM Attendance a WHERE a.user.userId = :userId " +
           "AND a.checkIn IS NOT NULL " +
           "AND a.checkoutOut IS NULL " +
           "AND DATE(a.createdAt) BETWEEN :startDate AND :endDate " +
           "ORDER BY a.createdAt DESC")
    List<Attendance> findMissedCheckOutRecordsByUser(
        @Param("userId") UUID userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Find present records for specific user
     */
    @Query("SELECT a FROM Attendance a WHERE a.user.userId = :userId " +
           "AND a.checkIn IS NOT NULL " +
           "AND a.checkoutOut IS NOT NULL " +
           "AND a.checkinStatus = 'CHECKIN' " +
           "AND DATE(a.createdAt) BETWEEN :startDate AND :endDate " +
           "ORDER BY a.createdAt DESC")
    List<Attendance> findPresentRecordsByUser(
        @Param("userId") UUID userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Find absent records for specific user
     */
    @Query("SELECT a FROM Attendance a WHERE a.user.userId = :userId " +
           "AND a.checkIn IS NULL " +
           "AND a.dateStatus IN ('WEEKDAY', 'OVERTIME') " +
           "AND DATE(a.createdAt) BETWEEN :startDate AND :endDate " +
           "ORDER BY a.createdAt DESC")
    List<Attendance> findAbsentRecordsByUser(
        @Param("userId") UUID userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}