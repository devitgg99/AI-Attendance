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
}


