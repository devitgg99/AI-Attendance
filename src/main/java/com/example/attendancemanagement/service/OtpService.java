package com.example.attendancemanagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String OTP_PREFIX = "otp:";
    private static final String EMAIL_PREFIX = "email:";
    private static final Duration OTP_EXPIRY = Duration.ofSeconds(60); // 60 seconds expiry
    private static final int MAX_ATTEMPTS = 3;
    private static final Duration ATTEMPT_RESET_DURATION = Duration.ofMinutes(15); // 15 minutes

    /**
     * Generate and store OTP for email
     */
    public String generateOtp(String email) {
        try {
            // Generate 6-digit OTP
            String otp = String.format("%06d", new Random().nextInt(1_000_000));
            
            // Store OTP in Redis with expiry
            String otpKey = OTP_PREFIX + email;
            redisTemplate.opsForValue().set(otpKey, otp, OTP_EXPIRY);
            
            // Store email with timestamp for tracking
            String emailKey = EMAIL_PREFIX + email;
            redisTemplate.opsForValue().set(emailKey, System.currentTimeMillis(), OTP_EXPIRY);
            
            // Reset attempt counter
            String attemptKey = "attempts:" + email;
            redisTemplate.opsForValue().set(attemptKey, 0, ATTEMPT_RESET_DURATION);
            
            log.info("Generated and stored OTP for email: {}", email);
            return otp;
        } catch (Exception e) {
            log.error("Failed to generate OTP for email {}: {}", email, e.getMessage());
            // Fallback: generate OTP without Redis storage
            return String.format("%06d", new Random().nextInt(1_000_000));
        }
    }

    /**
     * Verify OTP for email
     */
    public boolean verifyOtp(String email, String otp) {
        try {
            // Check attempt limit
            String attemptKey = "attempts:" + email;
            Integer attempts = (Integer) redisTemplate.opsForValue().get(attemptKey);
            if (attempts != null && attempts >= MAX_ATTEMPTS) {
                log.warn("Maximum attempts exceeded for email: {}", email);
                return false;
            }
            
            // Get stored OTP
            String otpKey = OTP_PREFIX + email;
            String storedOtp = (String) redisTemplate.opsForValue().get(otpKey);
            
            if (storedOtp == null) {
                log.warn("No OTP found for email: {}", email);
                return false;
            }
            
            // Verify OTP
            boolean isValid = storedOtp.equals(otp);
            
            if (isValid) {
                // Clear OTP after successful verification
                redisTemplate.delete(otpKey);
                redisTemplate.delete(EMAIL_PREFIX + email);
                redisTemplate.delete(attemptKey);
                log.info("OTP verified successfully for email: {}", email);
            } else {
                // Increment attempt counter
                int currentAttempts = attempts != null ? attempts : 0;
                redisTemplate.opsForValue().set(attemptKey, currentAttempts + 1, ATTEMPT_RESET_DURATION);
                log.warn("Invalid OTP attempt for email: {} (attempt {})", email, currentAttempts + 1);
            }
            
            return isValid;
        } catch (Exception e) {
            log.error("Failed to verify OTP for email {}: {}", email, e.getMessage());
            return false;
        }
    }

    /**
     * Check if OTP exists for email
     */
    public boolean hasOtp(String email) {
        try {
            String otpKey = OTP_PREFIX + email;
            return redisTemplate.hasKey(otpKey);
        } catch (Exception e) {
            log.error("Failed to check OTP existence for email {}: {}", email, e.getMessage());
            return false;
        }
    }

    /**
     * Get remaining time for OTP expiry
     */
    public Long getOtpExpiryTime(String email) {
        try {
            String otpKey = OTP_PREFIX + email;
            return redisTemplate.getExpire(otpKey);
        } catch (Exception e) {
            log.error("Failed to get OTP expiry time for email {}: {}", email, e.getMessage());
            return null;
        }
    }

    /**
     * Get remaining attempts for email
     */
    public Integer getRemainingAttempts(String email) {
        try {
            String attemptKey = "attempts:" + email;
            Integer attempts = (Integer) redisTemplate.opsForValue().get(attemptKey);
            return attempts != null ? MAX_ATTEMPTS - attempts : MAX_ATTEMPTS;
        } catch (Exception e) {
            log.error("Failed to get remaining attempts for email {}: {}", email, e.getMessage());
            return MAX_ATTEMPTS;
        }
    }

    /**
     * Clear OTP for email (manual cleanup)
     */
    public void clearOtp(String email) {
        try {
            String otpKey = OTP_PREFIX + email;
            String emailKey = EMAIL_PREFIX + email;
            String attemptKey = "attempts:" + email;
            
            redisTemplate.delete(otpKey);
            redisTemplate.delete(emailKey);
            redisTemplate.delete(attemptKey);
            
            log.info("Cleared OTP for email: {}", email);
        } catch (Exception e) {
            log.error("Failed to clear OTP for email {}: {}", email, e.getMessage());
        }
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
