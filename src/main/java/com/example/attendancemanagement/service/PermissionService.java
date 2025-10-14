package com.example.attendancemanagement.service;

import com.example.attendancemanagement.dto.PermissionDtos;
import com.example.attendancemanagement.entity.Permission;
import com.example.attendancemanagement.entity.User;
import com.example.attendancemanagement.enums.PermissionCategory;
import com.example.attendancemanagement.enums.PermissionStatus;
import com.example.attendancemanagement.enums.Shift;
import com.example.attendancemanagement.exception.BadRequestException;
import com.example.attendancemanagement.exception.NotFoundException;
import com.example.attendancemanagement.repository.PermissionRepository;
import com.example.attendancemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;

    @Transactional
    public PermissionDtos.PermissionResponse createPermissionRequest(PermissionDtos.CreatePermissionRequest request) {
        // Get current user from JWT token
        User currentUser = getCurrentUser();
        
        // Validate request based on category
        validatePermissionRequest(request);
        
        // Create permission entity
        Permission permission = new Permission();
        permission.setUser(currentUser);
        permission.setCategory(request.getCategory());
        permission.setRequestDate(request.getRequestDate());
        permission.setReason(request.getReason());
        permission.setShift(request.getShift());
        permission.setStatus(PermissionStatus.PENDING);
        
        // Set times based on category
        setTimesBasedOnCategory(permission, request);
        
        // Save permission
        Permission savedPermission = permissionRepository.save(permission);
        
        log.info("Permission request created: {} for user: {}", savedPermission.getPermissionId(), currentUser.getEmail());
        
        return mapToPermissionResponse(savedPermission);
    }

    private void validatePermissionRequest(PermissionDtos.CreatePermissionRequest request) {
        PermissionCategory category = request.getCategory();
        
        switch (category) {
            case EARLY_LEAVE:
                validateEarlyLeave(request);
                break;
            case GO_OUTSIDE:
                validateGoOutside(request);
                break;
            case LATE:
                validateLate(request);
                break;
            case PERMISSION:
                validatePermission(request);
                break;
            default:
                // For other categories, basic validation
                if (request.getStartTime() == null || request.getEndTime() == null) {
                    throw new BadRequestException("Start time and end time are required for this category");
                }
                break;
        }
    }

    private void validateEarlyLeave(PermissionDtos.CreatePermissionRequest request) {
        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new BadRequestException("Start time and end time are required for early leave");
        }
        
        Duration duration = Duration.between(request.getStartTime(), request.getEndTime());
        long hours = duration.toHours();
        
        if (hours < 2) {
            throw new BadRequestException("Early leave must be for 2 hours or more");
        }
    }

    private void validateGoOutside(PermissionDtos.CreatePermissionRequest request) {
        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new BadRequestException("Start time and end time are required for go outside");
        }
        
        Duration duration = Duration.between(request.getStartTime(), request.getEndTime());
        long hours = duration.toHours();
        
        if (hours >= 2) {
            throw new BadRequestException("Go outside must be under 2 hours");
        }
    }

    private void validateLate(PermissionDtos.CreatePermissionRequest request) {
        if (request.getStartTime() == null) {
            throw new BadRequestException("Start time is required for late permission");
        }
        // End time can be blank for late permission
    }

    private void validatePermission(PermissionDtos.CreatePermissionRequest request) {
        if (request.getShift() == null) {
            throw new BadRequestException("Shift is required for permission category");
        }
        // Start time and end time will be auto-set based on shift
    }

    private void setTimesBasedOnCategory(Permission permission, PermissionDtos.CreatePermissionRequest request) {
        PermissionCategory category = request.getCategory();
        
        if (category == PermissionCategory.PERMISSION) {
            // Auto-set times based on user shift
            setTimesBasedOnShift(permission, request.getShift());
        } else {
            // Use provided times for other categories
            permission.setStartTime(request.getStartTime());
            permission.setEndTime(request.getEndTime());
        }
    }

    private void setTimesBasedOnShift(Permission permission, Shift shift) {
        if (shift == Shift.MORNING) {
            permission.setStartTime(LocalTime.of(8, 0)); // 8:00 AM
            permission.setEndTime(LocalTime.of(12, 0));   // 12:00 PM
        } else if (shift == Shift.AFTERNOON) {
            permission.setStartTime(LocalTime.of(13, 0)); // 1:00 PM
            permission.setEndTime(LocalTime.of(17, 0));   // 5:00 PM
        }
    }

    public List<PermissionDtos.PermissionResponse> getMyPermissions() {
        User currentUser = getCurrentUser();
        List<Permission> permissions = permissionRepository.findByUserUserId(currentUser.getUserId());
        
        return permissions.stream()
                .map(this::mapToPermissionResponse)
                .collect(Collectors.toList());
    }

    public PermissionDtos.PermissionResponse getPermissionById(UUID permissionId) {
        User currentUser = getCurrentUser();
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new NotFoundException("Permission not found"));
        
        // Ensure the permission belongs to the current user
        if (!permission.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new NotFoundException("Permission not found");
        }
        
        return mapToPermissionResponse(permission);
    }

    @Transactional
    public PermissionDtos.PermissionResponse updatePermissionRequest(UUID permissionId, PermissionDtos.CreatePermissionRequest request) {
        User currentUser = getCurrentUser();
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new NotFoundException("Permission not found"));
        
        // Ensure the permission belongs to the current user
        if (!permission.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new NotFoundException("Permission not found");
        }
        
        // Check if permission can be updated (only if status is PENDING)
        if (permission.getStatus() != PermissionStatus.PENDING) {
            throw new BadRequestException("Cannot update permission request. Status is " + permission.getStatus() + ". Only PENDING requests can be updated.");
        }
        
        // Validate request based on category
        validatePermissionRequest(request);
        
        // Update permission fields
        permission.setCategory(request.getCategory());
        permission.setRequestDate(request.getRequestDate());
        permission.setReason(request.getReason());
        permission.setShift(request.getShift());
        
        // Set times based on category
        setTimesBasedOnCategory(permission, request);
        
        // Save updated permission
        Permission updatedPermission = permissionRepository.save(permission);
        
        log.info("Permission request updated: {} for user: {}", updatedPermission.getPermissionId(), currentUser.getEmail());
        
        return mapToPermissionResponse(updatedPermission);
    }

    @Transactional
    public PermissionDtos.PermissionResponse updatePermissionStatus(UUID permissionId, PermissionDtos.UpdatePermissionStatusRequest request) {
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new NotFoundException("Permission not found"));
        
        permission.setStatus(request.getStatus());
        Permission updatedPermission = permissionRepository.save(permission);
        
        log.info("Permission status updated: {} to {}", permissionId, request.getStatus());
        
        return mapToPermissionResponse(updatedPermission);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("User not authenticated");
        }
        
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private PermissionDtos.PermissionResponse mapToPermissionResponse(Permission permission) {
        PermissionDtos.PermissionResponse response = new PermissionDtos.PermissionResponse();
        response.setPermissionId(permission.getPermissionId());
        response.setUserId(permission.getUser().getUserId());
        response.setCategory(permission.getCategory());
        response.setRequestDate(permission.getRequestDate());
        response.setStartTime(permission.getStartTime());
        response.setEndTime(permission.getEndTime());
        response.setShift(permission.getShift());
        response.setReason(permission.getReason());
        response.setStatus(permission.getStatus());
        response.setCreatedAt(permission.getCreatedAt().toString());
        response.setUpdatedAt(permission.getUpdatedAt().toString());
        return response;
    }

}
