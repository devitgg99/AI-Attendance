package com.example.attendancemanagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    // private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String OTP_PREFIX = "otp:";
    private static final String EMAIL_PREFIX = "email:";
    private static final Duration OTP_EXPIRY = Duration.ofMinutes(5); // 5 minutes expiry
    private static final int MAX_ATTEMPTS = 3;
    private static final Duration ATTEMPT_RESET_DURATION = Duration.ofMinutes(15); // 15 minutes

    /**
     * Generate and store OTP for email
     */
    public String generateOtp(String email) {
        // Temporary fallback: generate OTP without Redis storage
        log.info("Generating OTP for email: {} (Redis disabled)", email);
        return String.format("%06d", new Random().nextInt(1_000_000));
    }

    /**
     * Verify OTP for email
     */
    public boolean verifyOtp(String email, String otp) {
        // Temporary fallback: accept any OTP for testing
        log.info("Verifying OTP for email: {} (Redis disabled)", email);
        return otp != null && otp.length() == 6;
    }

    /**
     * Check if OTP exists for email
     */
    public boolean hasOtp(String email) {
        // Temporary fallback: always return false
        return false;
    }

    /**
     * Get remaining time for OTP expiry
     */
    public Long getOtpExpiryTime(String email) {
        // Temporary fallback: return null
        return null;
    }

    /**
     * Get remaining attempts for email
     */
    public Integer getRemainingAttempts(String email) {
        // Temporary fallback: return max attempts
        return MAX_ATTEMPTS;
    }

    /**
     * Clear OTP for email (manual cleanup)
     */
    public void clearOtp(String email) {
        log.info("Cleared OTP for email: {} (Redis disabled)", email);
    }

    /**
     * Generate OTP for user ID (alternative method)
     */
    public String generateOtpForUser(UUID userId) {
        String email = userId.toString(); // Using user ID as identifier
        return generateOtp(email);
    }

    /**
     * Verify OTP for user ID (alternative method)
     */
    public boolean verifyOtpForUser(UUID userId, String otp) {
        String email = userId.toString(); // Using user ID as identifier
        return verifyOtp(email, otp);
    }
}
