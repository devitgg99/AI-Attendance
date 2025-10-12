package com.example.attendancemanagement.controller;

import com.example.attendancemanagement.dto.ApiResponse;
import com.example.attendancemanagement.dto.AuthDtos.CheckInResponse;
import com.example.attendancemanagement.dto.AuthDtos.CheckOutResponse;
import com.example.attendancemanagement.service.AttendanceService;
import com.example.attendancemanagement.util.TokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
    private final TokenUtil tokenUtil;

    @PostMapping("/checkin")
    @Operation(summary = "Check-in", description = "Record user check-in with time validation. Before 8:01 AM = on time, after 8:01 AM = late. Date status: weekday, overtime, weekend.")
    public ResponseEntity<ApiResponse<CheckInResponse>> checkIn(
            HttpServletRequest httpRequest) {
        
        // Get user ID from JWT token
        UUID userId = tokenUtil.getUserIdFromToken(httpRequest);
        
        CheckInResponse checkInResponse = attendanceService.checkIn(userId);
        
        ApiResponse<CheckInResponse> response = ApiResponse.<CheckInResponse>builder()
                .success(true)
                .message("Check-in recorded successfully")
                .payload(checkInResponse)
                .status(HttpStatus.CREATED)
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/checkout")
    @Operation(summary = "Check-out", description = "Record user check-out with time validation. Must be after 5:00 PM. Date status: weekday, overtime, weekend.")
    public ResponseEntity<ApiResponse<CheckOutResponse>> checkOut(
            HttpServletRequest httpRequest) {
        
        // Get user ID from JWT token
        UUID userId = tokenUtil.getUserIdFromToken(httpRequest);
        
        CheckOutResponse checkOutResponse = attendanceService.checkOut(userId);
        
        ApiResponse<CheckOutResponse> response = ApiResponse.<CheckOutResponse>builder()
                .success(true)
                .message("Check-out recorded successfully")
                .payload(checkOutResponse)
                .status(HttpStatus.OK)
                .build();
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
