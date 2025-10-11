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
        private Map<String, Object> userInfo; // JSON object containing role and other info
    }

    @Data
    public static class UserInfoResponse {
        private String userId;
        private String email;
        @JsonProperty("full_name")
        private String fullName;
        @JsonProperty("user_info")
        private Map<String, Object> userInfo; // JSON object containing role and other info
        private String createdAt;
        private String updatedAt;
    }
}


