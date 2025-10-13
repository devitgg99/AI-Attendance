package com.example.attendancemanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@Data
public class AuthDtos {
    @Data
    public static class LoginRequest {
        @Email @NotBlank
        private String email;
        @NotBlank
        private String password;
        private String deviceId; // nullable for admin
        private String fcmToken;
    }

    @Data
    public static class TokenResponse {
        private String uerRole;
        private String accessToken;
        private String refreshToken;
    }

    @Data
    public static class RefreshRequest {
        @NotBlank
        private String refreshToken;
        private String deviceId;
    }

    @Data
    public static class LogoutRequest {
        @NotBlank
        private String refreshToken;
    }

    @Data
    public static class ChangePasswordRequest {
        @NotBlank
        private String oldPassword;
        @NotBlank @Size(min = 8)
        private String newPassword;
    }

    @Data
    public static class ForgotPasswordRequest {
        @Email @NotBlank
        private String email;
    }

    @Data
    public static class VerifyPinRequest {
        @Email @NotBlank
        private String email;
        @NotBlank @Size(min = 6, max = 6)
        private String pin;
        @NotBlank @Size(min = 8)
        private String newPassword;
    }

    // User info structure - each user has exactly one role
    // The user_info JSONB field will contain role-specific information
    
    // Example formats for user_info JSONB field:
    
    // Admin user_info format:
    // {
    //   "role": "admin"
    // }
    
    // Staff user_info format:
    // {
    //   "role": "staff",
    //   "phone": "098765432",
    //   "date_of_birth": "2002-09-15",
    //   "place_of_birth": "Siem Reap",
    //   "phone_number": "099999999",
    //   "image_url": "http://example.com/staff.jpg",
    //   "current_address": "phnom penh, dong kao",
    //   "position": "IT instructor"
    // }
    
    // Student user_info format:
    // {
    //   "role": "student",
    //   "university": "royal university of phnom penh",
    //   "date_of_birth": "2002-09-15",
    //   "place_of_birth": "Siem Reap",
    //   "phone_number": "099999999",
    //   "image_url": "http://example.com/student.jpg"
    // }

    @Data
    public static class RegisterRequest {
        @Email @NotBlank
        private String email;
        @NotBlank @Size(min = 8)
        private String password;
        @JsonProperty("full_name")
        @NotBlank
        private String fullName;
        @JsonProperty("user_info")
        private Map<String, Object> userInfo; // JSONB field - each user has exactly one role with role-specific fields
    }

    @Data
    public static class UserInfoResponse {
        private String userId;
        private String email;
        @JsonProperty("full_name")
        private String fullName;
        @JsonProperty("user_info")
        private Map<String, Object> userInfo; // JSONB field - each user has exactly one role with role-specific fields
        private String createdAt;
        private String updatedAt;
    }

    @Data
    public static class CheckInRequest {
        // No additional fields needed for check-in
    }

    @Data
    public static class CheckInResponse {
        @JsonProperty("attendance_id")
        private String attendanceId;
        @JsonProperty("user_id")
        private String userId;
        @JsonProperty("attendance_date")
        private String attendanceDate;
        @JsonProperty("check_in_time")
        private String checkInTime;
        @JsonProperty("checkin_status")
        private String checkInStatus;
        @JsonProperty("message")
        private String message;
    }

    @Data
    public static class CheckOutRequest {
        // No additional fields needed for check-out
    }

    @Data
    public static class CheckOutResponse {
        @JsonProperty("attendance_id")
        private String attendanceId;
        @JsonProperty("user_id")
        private String userId;
        @JsonProperty("attendance_date")
        private String attendanceDate;
        @JsonProperty("check_in_time")
        private String checkInTime;
        @JsonProperty("check_out_time")
        private String checkOutTime;
        @JsonProperty("checkin_status")
        private String checkInStatus;
        @JsonProperty("checkout_status")
        private String checkOutStatus;
        @JsonProperty("date_status")
        private String dateStatus;
        @JsonProperty("message")
        private String message;
    }


    @Data
    public static class AttendanceRecord {
        @JsonProperty("attendance_id")
        private String attendanceId;
        @JsonProperty("attendance_date")
        private String attendanceDate;
        @JsonProperty("check_in_time")
        private String checkInTime;
        @JsonProperty("check_out_time")
        private String checkOutTime;
        @JsonProperty("checkin_status")
        private String checkInStatus;
        @JsonProperty("checkout_status")
        private String checkOutStatus;
        @JsonProperty("date_status")
        private String dateStatus;
        @JsonProperty("checkin_datetime")
        private String checkInDateTime;
        @JsonProperty("checkout_datetime")
        private String checkOutDateTime;
    }

    @Data
    public static class AttendanceStatusResponse {
        @JsonProperty("attendance_records")
        private java.util.List<AttendanceRecord> attendanceRecords;
    }
}


