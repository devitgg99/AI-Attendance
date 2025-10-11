package com.example.attendancemanagement.controller;

import com.example.attendancemanagement.dto.AuthDtos.*;
import com.example.attendancemanagement.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Authentication", description = "APIs for user authentication and authorization")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return access/refresh tokens. For students: Device ID required. Device mismatch sends notification and blocks login.")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Generate new access token using valid refresh token")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Deactivate user session using refresh token")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/change-password/{userId}")
    @Operation(summary = "Change password", description = "Change user password with old password verification")
    public ResponseEntity<Void> changePassword(
            @Parameter(description = "User ID") @PathVariable UUID userId, 
            @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(userId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset", description = "Send PIN to user's registered device for password reset")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.requestForgotPassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-pin")
    @Operation(summary = "Verify PIN and reset password", description = "Verify PIN and set new password")
    public ResponseEntity<Void> verifyPin(@Valid @RequestBody VerifyPinRequest request) {
        authService.verifyPinAndReset(request);
        return ResponseEntity.ok().build();
    }
}


