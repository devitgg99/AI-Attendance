package com.example.attendancemanagement.repository;

import com.example.attendancemanagement.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {
    
    List<UserSession> findByUserUserId(UUID userId);
    
    Optional<UserSession> findByHarshRefreshToken(String harshRefreshToken);
    
    List<UserSession> findByUserUserIdOrderByCreatedAtDesc(UUID userId);
    
    Optional<UserSession> findTopByUserUserIdOrderByCreatedAtDesc(UUID userId);
    
    Optional<UserSession> findByDeviceId(String deviceId);
    
    Optional<UserSession> findByDeviceIdAndUserUserId(String deviceId, UUID userId);
    
    Optional<UserSession> findByPinHarsh(String pinHarsh);
}

