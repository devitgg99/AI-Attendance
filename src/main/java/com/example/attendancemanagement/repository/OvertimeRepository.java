package com.example.attendancemanagement.repository;

import com.example.attendancemanagement.entity.Overtime;
import com.example.attendancemanagement.enums.OvertimeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OvertimeRepository extends JpaRepository<Overtime, UUID> {
    
    /**
     * Find all overtime requests for a specific user
     */
    List<Overtime> findByUserUserIdOrderByRequestDateDesc(UUID userId);
    
    /**
     * Find overtime requests by status
     */
    List<Overtime> findByStatusOrderByRequestDateDesc(OvertimeStatus status);
    
    /**
     * Find overtime requests for a user by status
     */
    List<Overtime> findByUserUserIdAndStatusOrderByRequestDateDesc(UUID userId, OvertimeStatus status);
    
    /**
     * Find overtime requests for a specific date
     */
    @Query("SELECT o FROM Overtime o WHERE o.requestDate = :date ORDER BY o.requestDate DESC")
    List<Overtime> findByRequestDateOrderByRequestDateDesc(@Param("date") LocalDate date);
    
    /**
     * Find overtime requests within date range
     */
    @Query("SELECT o FROM Overtime o WHERE o.requestDate BETWEEN :startDate AND :endDate ORDER BY o.requestDate DESC")
    List<Overtime> findByRequestDateBetweenOrderByRequestDateDesc(
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * Find overtime requests for a user within date range
     */
    @Query("SELECT o FROM Overtime o WHERE o.user.userId = :userId AND o.requestDate BETWEEN :startDate AND :endDate ORDER BY o.requestDate DESC")
    List<Overtime> findByUserUserIdAndRequestDateBetweenOrderByRequestDateDesc(
        @Param("userId") UUID userId,
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * Find overtime requests for a user by status within date range
     */
    @Query("SELECT o FROM Overtime o WHERE o.user.userId = :userId AND o.status = :status AND o.requestDate BETWEEN :startDate AND :endDate ORDER BY o.requestDate DESC")
    List<Overtime> findByUserUserIdAndStatusAndRequestDateBetweenOrderByRequestDateDesc(
        @Param("userId") UUID userId,
        @Param("status") OvertimeStatus status,
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * Find overtime requests by status within date range
     */
    @Query("SELECT o FROM Overtime o WHERE o.status = :status AND o.requestDate BETWEEN :startDate AND :endDate ORDER BY o.requestDate DESC")
    List<Overtime> findByStatusAndRequestDateBetweenOrderByRequestDateDesc(
        @Param("status") OvertimeStatus status,
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * Find overtime requests for weekdays only
     */
    @Query("SELECT o FROM Overtime o WHERE o.isWeekday = true ORDER BY o.requestDate DESC")
    List<Overtime> findByIsWeekdayTrueOrderByRequestDateDesc();
    
    /**
     * Find overtime requests for weekends only
     */
    @Query("SELECT o FROM Overtime o WHERE o.isWeekday = false ORDER BY o.requestDate DESC")
    List<Overtime> findByIsWeekdayFalseOrderByRequestDateDesc();
    
    /**
     * Count overtime requests by status
     */
    long countByStatus(OvertimeStatus status);
    
    /**
     * Count overtime requests for a user
     */
    long countByUserUserId(UUID userId);
    
    /**
     * Count overtime requests for a user by status
     */
    long countByUserUserIdAndStatus(UUID userId, OvertimeStatus status);
    
    /**
     * Check if overtime request exists for user and date
     */
    @Query("SELECT COUNT(o) > 0 FROM Overtime o WHERE o.user.userId = :userId AND o.requestDate = :date")
    boolean existsByUserUserIdAndRequestDate(@Param("userId") UUID userId, @Param("date") LocalDate date);
    
    /**
     * Find overtime request by user and date
     */
    @Query("SELECT o FROM Overtime o WHERE o.user.userId = :userId AND o.requestDate = :date")
    Optional<Overtime> findByUserUserIdAndRequestDate(@Param("userId") UUID userId, @Param("date") LocalDate date);
}
