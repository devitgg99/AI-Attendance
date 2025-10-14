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
}


