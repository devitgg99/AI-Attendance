package com.example.attendancemanagement.repository;

import com.example.attendancemanagement.entity.Permission;
import com.example.attendancemanagement.enums.PermissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    
    List<Permission> findByUserUserId(UUID userId);
    
    List<Permission> findByUserUserIdAndStatus(UUID userId, PermissionStatus status);
    
    List<Permission> findByStatus(PermissionStatus status);
    
    @Query("SELECT p FROM Permission p WHERE p.user.userId = :userId AND p.requestDate BETWEEN :startDate AND :endDate")
    List<Permission> findByUserAndDateRange(@Param("userId") UUID userId, 
                                          @Param("startDate") LocalDate startDate, 
                                          @Param("endDate") LocalDate endDate);
    
    @Query("SELECT p FROM Permission p WHERE p.requestDate BETWEEN :startDate AND :endDate")
    List<Permission> findByDateRange(@Param("startDate") LocalDate startDate, 
                                    @Param("endDate") LocalDate endDate);
}
