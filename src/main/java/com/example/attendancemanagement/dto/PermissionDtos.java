package com.example.attendancemanagement.dto;

import com.example.attendancemanagement.enums.PermissionCategory;
import com.example.attendancemanagement.enums.PermissionStatus;
import com.example.attendancemanagement.enums.Shift;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class PermissionDtos {
    
    @Data
    public static class CreatePermissionRequest {
        @NotNull
        private PermissionCategory category;
        
        @JsonProperty("request_date")
        @NotNull
        private LocalDate requestDate;
        
        @JsonProperty("start_time")
        private LocalTime startTime;
        
        @JsonProperty("end_time")
        private LocalTime endTime;
        
        private Shift shift;
        
        @NotBlank
        private String reason;
    }
    
    @Data
    public static class UpdatePermissionStatusRequest {
        @NotNull
        private PermissionStatus status;
        
        private String adminComment; // Optional comment from admin
    }
    
    @Data
    public static class PermissionResponse {
        @JsonProperty("permission_id")
        private UUID permissionId;
        
        @JsonProperty("user_id")
        private UUID userId;
        
        private PermissionCategory category;
        
        @JsonProperty("request_date")
        private LocalDate requestDate;
        
        @JsonProperty("start_time")
        private LocalTime startTime;
        
        @JsonProperty("end_time")
        private LocalTime endTime;
        
        private Shift shift;
        
        private String reason;
        
        private PermissionStatus status;
        
        @JsonProperty("created_at")
        private String createdAt;
        
        @JsonProperty("updated_at")
        private String updatedAt;
        
        @JsonProperty("admin_comment")
        private String adminComment;
    }
    
    @Data
    public static class PermissionListResponse {
        @JsonProperty("permission_id")
        private UUID permissionId;
        
        @JsonProperty("user_name")
        private String userName;
        
        @JsonProperty("user_email")
        private String userEmail;
        
        private PermissionCategory category;
        
        @JsonProperty("request_date")
        private LocalDate requestDate;
        
        @JsonProperty("start_time")
        private LocalTime startTime;
        
        @JsonProperty("end_time")
        private LocalTime endTime;
        
        private Shift shift;
        
        private String reason;
        
        private PermissionStatus status;
        
        @JsonProperty("created_at")
        private String createdAt;
        
        @JsonProperty("updated_at")
        private String updatedAt;
    }
}
