package com.example.attendancemanagement.controller;

import com.example.attendancemanagement.dto.ApiResponse;
import com.example.attendancemanagement.dto.OvertimeDtos.RequestOvertime;
import com.example.attendancemanagement.dto.OvertimeDtos.OvertimeResponse;
import com.example.attendancemanagement.service.OvertimeService;
import com.example.attendancemanagement.util.TokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/overtime")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class OvertimeController {

	private final OvertimeService overtimeService;
	private final TokenUtil tokenUtil;

	@PostMapping("/request")
	@Operation(summary = "Request overtime", description = "Create an overtime request. User id is fetched from token.")
	public ResponseEntity<ApiResponse<OvertimeResponse>> requestOvertime(
			@Valid @RequestBody RequestOvertime body,
			HttpServletRequest httpRequest) {

		UUID userIdFromToken = tokenUtil.getUserIdFromToken(httpRequest);

		OvertimeResponse payload = overtimeService.requestOvertime(userIdFromToken, body);

		ApiResponse<OvertimeResponse> response = ApiResponse.<OvertimeResponse>builder()
				.success(true)
				.message("Overtime request created successfully")
				.payload(payload)
				.status(HttpStatus.CREATED)
				.build();

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/my-overtime")
	@Operation(summary = "Get my overtime requests", description = "Get all overtime requests for the current user")
	public ResponseEntity<ApiResponse<java.util.List<OvertimeResponse>>> getMyOvertimeRequests(
			HttpServletRequest httpRequest) {

		UUID userIdFromToken = tokenUtil.getUserIdFromToken(httpRequest);
		java.util.List<OvertimeResponse> overtimeRequests = overtimeService.getMyOvertimeRequests(userIdFromToken);

		ApiResponse<java.util.List<OvertimeResponse>> response = ApiResponse.<java.util.List<OvertimeResponse>>builder()
				.success(true)
				.message("Overtime requests retrieved successfully")
				.payload(overtimeRequests)
				.status(HttpStatus.OK)
				.build();

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{overtimeId}")
	@Operation(summary = "Get overtime by ID", description = "Get a specific overtime request by ID for the current user")
	public ResponseEntity<ApiResponse<OvertimeResponse>> getOvertimeById(
			@PathVariable UUID overtimeId,
			HttpServletRequest httpRequest) {

		UUID userIdFromToken = tokenUtil.getUserIdFromToken(httpRequest);
		OvertimeResponse overtime = overtimeService.getOvertimeById(overtimeId, userIdFromToken);

		ApiResponse<OvertimeResponse> response = ApiResponse.<OvertimeResponse>builder()
				.success(true)
				.message("Overtime request retrieved successfully")
				.payload(overtime)
				.status(HttpStatus.OK)
				.build();

		return ResponseEntity.ok(response);
	}

	@PutMapping("/{overtimeId}")
	@Operation(summary = "Update overtime request", description = "Update an overtime request (only if status is PENDING)")
	public ResponseEntity<ApiResponse<OvertimeResponse>> updateOvertimeRequest(
			@PathVariable UUID overtimeId,
			@Valid @RequestBody RequestOvertime body,
			HttpServletRequest httpRequest) {

		UUID userIdFromToken = tokenUtil.getUserIdFromToken(httpRequest);
		OvertimeResponse payload = overtimeService.updateOvertimeRequest(overtimeId, userIdFromToken, body);

		ApiResponse<OvertimeResponse> response = ApiResponse.<OvertimeResponse>builder()
				.success(true)
				.message("Overtime request updated successfully")
				.payload(payload)
				.status(HttpStatus.OK)
				.build();

		return ResponseEntity.ok(response);
	}

	@PutMapping("/{overtimeId}/status")
	@Operation(summary = "Update overtime status (Admin)", description = "Approve or reject an overtime request")
	public ResponseEntity<ApiResponse<OvertimeResponse>> updateOvertimeStatus(
			@PathVariable UUID overtimeId,
			@Valid @RequestBody com.example.attendancemanagement.dto.OvertimeDtos.UpdateOvertimeStatusRequest request) {

		OvertimeResponse payload = overtimeService.updateOvertimeStatus(overtimeId, request);

		ApiResponse<OvertimeResponse> response = ApiResponse.<OvertimeResponse>builder()
				.success(true)
				.message("Overtime status updated successfully")
				.payload(payload)
				.status(HttpStatus.OK)
				.build();

		return ResponseEntity.ok(response);
	}
}



