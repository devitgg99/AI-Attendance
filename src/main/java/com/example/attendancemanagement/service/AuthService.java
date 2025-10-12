package com.example.attendancemanagement.service;

import com.example.attendancemanagement.dto.AuthDtos.*;
import com.example.attendancemanagement.entity.User;
import com.example.attendancemanagement.entity.UserSession;
import com.example.attendancemanagement.enums.UserStatus;
import com.example.attendancemanagement.exception.BadRequestException;
import com.example.attendancemanagement.exception.NotFoundException;
import com.example.attendancemanagement.exception.UnauthorizedException;
import com.example.attendancemanagement.repository.UserRepository;
import com.example.attendancemanagement.repository.UserSessionRepository;
import com.example.attendancemanagement.security.JwtTokenService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final JwtTokenService jwtTokenService;
    private final FirebaseService firebaseService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository,
                       UserSessionRepository userSessionRepository,
                       JwtTokenService jwtTokenService,
                       FirebaseService firebaseService) {
        this.userRepository = userRepository;
        this.userSessionRepository = userSessionRepository;
        this.jwtTokenService = jwtTokenService;
        this.firebaseService = firebaseService;
    }

    @Transactional
    public TokenResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }
        
        // Check user status
        if (user.getUserStatus() == UserStatus.DEACTIVATE) {
            throw new UnauthorizedException("Account is deactivated");
        }

        // Parse user_info Map to get role
        String userRole = getUserRoleFromMap(user.getUserInfo());
        
        // Device ID validation based on role
        if (!"admin".equals(userRole) && (req.getDeviceId() == null || req.getDeviceId().isBlank())) {
            throw new BadRequestException("Device ID required for non-admin users");
        }

        // FCM token validation - check if it matches existing token
        if (!validateFcmToken(user.getUserId(), req.getFcmToken(), userRole)) {
            throw new UnauthorizedException("FCM token mismatch");
        }

        if (!"admin".equals(userRole)) {
            // Enforce single device for non-admin users and notify on mismatch
            userSessionRepository.findTopByUserUserIdOrderByCreatedAtDesc(user.getUserId())
                    .ifPresent(existing -> {
                        if (existing.getDeviceId() != null && req.getDeviceId() != null && !existing.getDeviceId().equals(req.getDeviceId())) {
                            if (existing.getFcmToken() != null && !existing.getFcmToken().isBlank()) {
                                firebaseService.sendNotification(
                                        existing.getFcmToken(),
                                        "Security Alert",
                                        "Your account was attempted to login from a different device.",
                                        Map.of("userId", user.getUserId().toString())
                                );
                            }
                            throw new UnauthorizedException("Device ID mismatch");
                        }
                    });
        }

        Map<String, Object> claims = Map.of("uid", user.getUserId().toString(), "role", userRole);
        String access = jwtTokenService.generateAccessToken(user.getEmail(), claims);
        String refresh = jwtTokenService.generateRefreshToken(user.getEmail(), claims);

        // persist session with hashed refresh token
        if ("admin".equals(userRole)) {
            // Admin users can have multiple devices
            // Check if session with same device ID exists
            Optional<UserSession> existingSession = userSessionRepository.findByDeviceIdAndUserUserId(req.getDeviceId(), user.getUserId());
            
            UserSession session;
            if (existingSession.isPresent()) {
                // Update existing session (same device ID, update FCM token)
                session = existingSession.get();
            } else {
                // Create new session for admin (different device ID)
                session = new UserSession();
                session.setUser(user);
                session.setDeviceId(req.getDeviceId());
            }
            session.setFcmToken(req.getFcmToken());
            session.setHarshRefreshToken(passwordEncoder.encode(refresh));
            userSessionRepository.save(session);
        } else {
            // Non-admin (students): ensure only one session row exists
            // Check if device ID matches existing session
            Optional<UserSession> existingSession = userSessionRepository.findTopByUserUserIdOrderByCreatedAtDesc(user.getUserId());
            
            UserSession session;
            if (existingSession.isPresent() && existingSession.get().getDeviceId() != null && 
                existingSession.get().getDeviceId().equals(req.getDeviceId())) {
                // Update existing session (same device ID, FCM token can be different)
                session = existingSession.get();
            } else {
                // Create new session for student
                session = new UserSession();
                session.setUser(user);
                session.setDeviceId(req.getDeviceId());
            }
            session.setFcmToken(req.getFcmToken());
            session.setHarshRefreshToken(passwordEncoder.encode(refresh));
            userSessionRepository.save(session);
        }

        if (req.getFcmToken() != null && !req.getFcmToken().isBlank()) {
            firebaseService.sendNotification(req.getFcmToken(), "Login Success", "You have logged in.", Map.of());
        }

        TokenResponse resp = new TokenResponse();
        resp.setAccessToken(access);
        resp.setRefreshToken(refresh);
        return resp;
    }

    @Transactional
    public UserInfoResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        
        User user = new User();
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setFullName(req.getFullName());
        user.setUserInfo(req.getUserInfo()); // JSON string containing role and other info
        user.setUserStatus(UserStatus.ACTIVE); // Default to active status
        
        user = userRepository.save(user);

        UserInfoResponse res = new UserInfoResponse();
        res.setUserId(user.getUserId().toString());
        res.setEmail(user.getEmail());
        res.setFullName(user.getFullName());
        res.setUserInfo(user.getUserInfo());
        res.setCreatedAt(user.getCreatedAt() == null ? null : user.getCreatedAt().toString());
        res.setUpdatedAt(user.getUpdatedAt() == null ? null : user.getUpdatedAt().toString());
        return res;
    }

    // Helper method to extract role from user_info Map
    private String getUserRoleFromMap(Map<String, Object> userInfoMap) {
        if (userInfoMap == null || userInfoMap.isEmpty()) {
            return "student"; // default role
        }
        Object role = userInfoMap.get("role");
        return role != null ? role.toString() : "student";
    }

    // Helper method to validate FCM token
    private boolean validateFcmToken(UUID userId, String fcmToken, String userRole) {
        if (fcmToken == null || fcmToken.isBlank()) {
            return true; // FCM token is optional for some cases
        }
        
        // For admin users, FCM token validation is more lenient
        if ("admin".equals(userRole)) {
            return true;
        }
        
        // For non-admin users, check if FCM token matches existing session
        Optional<UserSession> existingSession = userSessionRepository.findTopByUserUserIdOrderByCreatedAtDesc(userId);
        if (existingSession.isPresent()) {
            String existingFcmToken = existingSession.get().getFcmToken();
            if (existingFcmToken != null && !existingFcmToken.isBlank()) {
                return existingFcmToken.equals(fcmToken);
            }
        }
        
        return true; // Allow if no existing session or FCM token
    }

    @Transactional
    public TokenResponse refresh(RefreshRequest req) {
        UserSession session = userSessionRepository.findAll().stream()
                .filter(s -> passwordEncoder.matches(req.getRefreshToken(), s.getHarshRefreshToken()))
                .findFirst()
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        User user = session.getUser();
        String userRole = getUserRoleFromMap(user.getUserInfo());
        Map<String, Object> claims = Map.of("uid", user.getUserId().toString(), "role", userRole);
        String access = jwtTokenService.generateAccessToken(user.getEmail(), claims);
        String refresh = jwtTokenService.generateRefreshToken(user.getEmail(), claims);
        session.setHarshRefreshToken(passwordEncoder.encode(refresh));
        userSessionRepository.save(session);

        TokenResponse resp = new TokenResponse();
        resp.setAccessToken(access);
        resp.setRefreshToken(refresh);
        return resp;
    }

    @Transactional
    public void logout(LogoutRequest req) {
        Optional<UserSession> match = userSessionRepository.findAll().stream()
                .filter(s -> passwordEncoder.matches(req.getRefreshToken(), s.getHarshRefreshToken()))
                .findFirst();
        match.ifPresent(userSessionRepository::delete);
    }

    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest req) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword()))
            throw new UnauthorizedException("Old password incorrect");
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void requestForgotPassword(ForgotPasswordRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));
        String pin = String.format("%06d", new java.util.Random().nextInt(1_000_000));
        
        // Store PIN hash in user session
        Optional<UserSession> existingSession = userSessionRepository.findTopByUserUserIdOrderByCreatedAtDesc(user.getUserId());
        if (existingSession.isPresent()) {
            UserSession session = existingSession.get();
            session.setPinHarsh(passwordEncoder.encode(pin));
            userSessionRepository.save(session);
        } else {
            // Create new session for PIN storage
            UserSession session = new UserSession();
            session.setUser(user);
            session.setPinHarsh(passwordEncoder.encode(pin));
            userSessionRepository.save(session);
        }
        
        // Send PIN via FCM if available
        Optional<UserSession> sessionWithFcm = userSessionRepository.findTopByUserUserIdOrderByCreatedAtDesc(user.getUserId());
        sessionWithFcm.ifPresent(session -> {
            if (session.getFcmToken() != null && !session.getFcmToken().isBlank()) {
                firebaseService.sendNotification(session.getFcmToken(), "Password Reset PIN", "PIN: " + pin, Map.of());
            }
        });
    }

    @Transactional
    public void verifyPinAndReset(VerifyPinRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));
        
        // Find session with PIN
        Optional<UserSession> sessionWithPin = userSessionRepository.findTopByUserUserIdOrderByCreatedAtDesc(user.getUserId());
        if (sessionWithPin.isEmpty() || sessionWithPin.get().getPinHarsh() == null || 
            !passwordEncoder.matches(req.getPin(), sessionWithPin.get().getPinHarsh())) {
            throw new UnauthorizedException("Invalid PIN");
        }
        
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
        
        // Clear PIN from session
        UserSession session = sessionWithPin.get();
        session.setPinHarsh(null);
        userSessionRepository.save(session);
    }
}


