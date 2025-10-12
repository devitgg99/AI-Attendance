package com.example.attendancemanagement.controller;

import com.example.attendancemanagement.dto.ApiResponse;
import com.example.attendancemanagement.entity.User;
import com.example.attendancemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.example.attendancemanagement.dto.AuthDtos.RegisterRequest;
import com.example.attendancemanagement.dto.AuthDtos.UserInfoResponse;
import com.example.attendancemanagement.service.AuthService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@Tag(name = "User Management", description = "APIs for user management operations")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve a list of all users in the system")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by their unique identifier")
    public ResponseEntity<User> getUserById(
            @Parameter(description = "User ID") @PathVariable UUID id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email", description = "Retrieve a specific user by their email address")
    public ResponseEntity<User> getUserByEmail(
            @Parameter(description = "User email address") @PathVariable String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create user (Admin)", description = "Create a new user with hashed password - Admin only")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().build();
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Register a new user account with validation. Default role is STUDENT if not specified.")
    public ResponseEntity<ApiResponse<UserInfoResponse>> register(@RequestBody RegisterRequest request) {
        UserInfoResponse userInfo = authService.register(request);
        ApiResponse<UserInfoResponse> response = ApiResponse.<UserInfoResponse>builder()
                .success(true)
                .message("User registered successfully")
                .payload(userInfo)
                .status(HttpStatus.CREATED)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

