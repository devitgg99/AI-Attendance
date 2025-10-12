package com.example.attendancemanagement.controller;

import com.example.attendancemanagement.dto.ApiResponse;
import com.example.attendancemanagement.dto.AuthDtos.CheckInRequest;
import com.example.attendancemanagement.dto.AuthDtos.CheckInResponse;
import com.example.attendancemanagement.service.AttendanceService;
import com.example.attendancemanagement.security.JwtTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final JwtTokenService jwtTokenService;

    @PostMapping("/checkin")
    @Operation(summary = "Check-in", description = "Record user check-in with time validation. Before 8:01 AM = on time, after 8:01 AM = late. Date status: weekday, overtime, weekend.")
    public ResponseEntity<ApiResponse<CheckInResponse>> checkIn(
            HttpServletRequest httpRequest) {
        
        // Get user ID from JWT token
        UUID userId = getUserIdFromToken(httpRequest);
        
        CheckInResponse checkInResponse = attendanceService.checkIn(userId);
        
        ApiResponse<CheckInResponse> response = ApiResponse.<CheckInResponse>builder()
                .success(true)
                .message("Check-in recorded successfully")
                .payload(checkInResponse)
                .status(HttpStatus.CREATED)
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private UUID getUserIdFromToken(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Authorization header missing or invalid");
        }

        final String jwt = authHeader.substring(7);
        try {
            // Extract user ID from JWT claims
            String userIdString = jwtTokenService.extractClaim(jwt, claims -> claims.get("uid", String.class));
            return UUID.fromString(userIdString);
        } catch (Exception e) {
            throw new RuntimeException("Invalid or expired token");
        }
    }
}
