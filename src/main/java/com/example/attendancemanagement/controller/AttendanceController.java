package com.example.attendancemanagement.controller;

import com.example.attendancemanagement.dto.ApiResponse;
import com.example.attendancemanagement.dto.AuthDtos.CheckInResponse;
import com.example.attendancemanagement.dto.AuthDtos.CheckOutResponse;
import com.example.attendancemanagement.dto.AuthDtos.AttendanceStatusResponse;
import com.example.attendancemanagement.enums.AttendanceStatus;
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
        UUID userIdFromToken = tokenUtil.getUserIdFromToken(httpRequest);

        CheckInResponse checkInResponse = attendanceService.checkIn(userIdFromToken);
        
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
        UUID userIdFromToken = tokenUtil.getUserIdFromToken(httpRequest);
        
        CheckOutResponse checkOutResponse = attendanceService.checkOut(userIdFromToken);
        
        ApiResponse<CheckOutResponse> response = ApiResponse.<CheckOutResponse>builder()
                .success(true)
                .message("Check-out recorded successfully")
                .payload(checkOutResponse)
                .status(HttpStatus.OK)
                .build();
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @GetMapping("/history")
    @Operation(summary = "Get attendance history", 
               description = "Get current user's attendance records by status. Optional query parameters: status, startDate, endDate. Default date range: first of current month to end of current month.")
    public ResponseEntity<ApiResponse<AttendanceStatusResponse>> getAttendanceHistory(
            @RequestParam(required = false) AttendanceStatus status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpServletRequest httpRequest) {
        
        // Get user ID from JWT token to filter by current user only
        UUID userIdFromToken = tokenUtil.getUserIdFromToken(httpRequest);
        
        AttendanceStatusResponse statusResponse = attendanceService.getAttendanceHistory(userIdFromToken, status, startDate, endDate);
        
        ApiResponse<AttendanceStatusResponse> response = ApiResponse.<AttendanceStatusResponse>builder()
                .success(true)
                .message("Attendance history retrieved successfully")
                .payload(statusResponse)
                .status(HttpStatus.OK)
                .build();
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/date")
    @Operation(summary = "Get attendance by specific date", 
               description = "Get current user's attendance record for a specific date. Defaults to current date if no date provided.")
    public ResponseEntity<ApiResponse<AttendanceStatusResponse>> getAttendanceByDate(
            @RequestParam(required = false) String date,
            HttpServletRequest httpRequest) {
        
        // Get user ID from JWT token to filter by current user only
        UUID userIdFromToken = tokenUtil.getUserIdFromToken(httpRequest);
        
        AttendanceStatusResponse statusResponse = attendanceService.getAttendanceByDate(userIdFromToken, date);
        
        ApiResponse<AttendanceStatusResponse> response = ApiResponse.<AttendanceStatusResponse>builder()
                .success(true)
                .message("Attendance records retrieved successfully for date: " + (date != null ? date : "current date"))
                .payload(statusResponse)
                .status(HttpStatus.OK)
                .build();
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
