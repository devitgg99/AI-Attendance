package com.example.attendancemanagement.controller;

import com.example.attendancemanagement.dto.ApiResponse;
import com.example.attendancemanagement.dto.PermissionDtos;
import com.example.attendancemanagement.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@Tag(name = "Permission Management", description = "APIs for managing permission requests")
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping("/request")
    @Operation(summary = "Request permission", description = "Submit a new permission request")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<PermissionDtos.PermissionResponse>> requestPermission(
            @Valid @RequestBody PermissionDtos.CreatePermissionRequest request) {
        
        PermissionDtos.PermissionResponse response = permissionService.createPermissionRequest(request);
        
        ApiResponse<PermissionDtos.PermissionResponse> apiResponse = ApiResponse.<PermissionDtos.PermissionResponse>builder()
                .success(true)
                .message("Permission request submitted successfully")
                .payload(response)
                .status(HttpStatus.CREATED)
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping("/my-permissions")
    @Operation(summary = "Get my permissions", description = "Get all permission requests for the current user")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<List<PermissionDtos.PermissionResponse>>> getMyPermissions() {
        List<PermissionDtos.PermissionResponse> permissions = permissionService.getMyPermissions();
        
        ApiResponse<List<PermissionDtos.PermissionResponse>> apiResponse = ApiResponse.<List<PermissionDtos.PermissionResponse>>builder()
                .success(true)
                .message("Permissions retrieved successfully")
                .payload(permissions)
                .status(HttpStatus.OK)
                .build();
        
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{permissionId}")
    @Operation(summary = "Get permission by ID", description = "Get a specific permission request by ID for the current user")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<PermissionDtos.PermissionResponse>> getPermissionById(
            @PathVariable UUID permissionId) {
        
        PermissionDtos.PermissionResponse permission = permissionService.getPermissionById(permissionId);
        
        ApiResponse<PermissionDtos.PermissionResponse> apiResponse = ApiResponse.<PermissionDtos.PermissionResponse>builder()
                .success(true)
                .message("Permission retrieved successfully")
                .payload(permission)
                .status(HttpStatus.OK)
                .build();
        
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{permissionId}")
    @Operation(summary = "Update permission request", description = "Update a permission request (only if status is PENDING)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<PermissionDtos.PermissionResponse>> updatePermissionRequest(
            @PathVariable UUID permissionId,
            @Valid @RequestBody PermissionDtos.CreatePermissionRequest request) {
        
        PermissionDtos.PermissionResponse response = permissionService.updatePermissionRequest(permissionId, request);
        
        ApiResponse<PermissionDtos.PermissionResponse> apiResponse = ApiResponse.<PermissionDtos.PermissionResponse>builder()
                .success(true)
                .message("Permission request updated successfully")
                .payload(response)
                .status(HttpStatus.OK)
                .build();
        
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{permissionId}/status")
    @Operation(summary = "Update permission status (Admin)", description = "Approve or reject a permission request")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<PermissionDtos.PermissionResponse>> updatePermissionStatus(
            @PathVariable UUID permissionId,
            @Valid @RequestBody PermissionDtos.UpdatePermissionStatusRequest request) {
        
        PermissionDtos.PermissionResponse response = permissionService.updatePermissionStatus(permissionId, request);
        
        ApiResponse<PermissionDtos.PermissionResponse> apiResponse = ApiResponse.<PermissionDtos.PermissionResponse>builder()
                .success(true)
                .message("Permission status updated successfully")
                .payload(response)
                .status(HttpStatus.OK)
                .build();
        
        return ResponseEntity.ok(apiResponse);
    }
}
