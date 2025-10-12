package com.example.attendancemanagement.util;

import com.example.attendancemanagement.exception.*;
import com.example.attendancemanagement.security.JwtTokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TokenUtil {
    
    private final JwtTokenService jwtTokenService;
    
    public TokenUtil(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }
    
    /**
     * Extract user ID from JWT token in the request
     * @param request HTTP request containing Authorization header
     * @return User ID from token
     * @throws MissingTokenException if Authorization header is missing
     * @throws InvalidTokenException if token format is invalid
     * @throws ExpiredTokenException if token has expired
     * @throws TokenException if there's an error processing the token
     */
    public UUID getUserIdFromToken(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        
        // Check if Authorization header is missing
        if (authHeader == null) {
            throw new MissingTokenException("Authorization header is required");
        }
        
        // Check if Authorization header has correct format
        if (!authHeader.startsWith("Bearer ")) {
            throw new InvalidTokenException("Authorization header must start with 'Bearer '");
        }

        final String jwt = authHeader.substring(7);
        
        // Check if token is empty after removing "Bearer "
        if (jwt.trim().isEmpty()) {
            throw new InvalidTokenException("Token cannot be empty");
        }
        
        try {
            // Extract user ID from JWT claims
            String userIdString = jwtTokenService.extractClaim(jwt, claims -> claims.get("uid", String.class));
            
            // Check if user ID is present in token
            if (userIdString == null || userIdString.trim().isEmpty()) {
                throw new InvalidTokenException("User ID not found in token");
            }
            
            return UUID.fromString(userIdString);
        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException("Invalid user ID format in token");
        } catch (Exception e) {
            // Check if it's a token expiration issue
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("expired")) {
                throw new ExpiredTokenException("Token has expired");
            }
            throw new TokenException("Failed to process token: " + e.getMessage());
        }
    }
    
    /**
     * Extract role from JWT token in the request
     * @param request HTTP request containing Authorization header
     * @return Role from token
     * @throws MissingTokenException if Authorization header is missing
     * @throws InvalidTokenException if token format is invalid
     * @throws ExpiredTokenException if token has expired
     * @throws TokenException if there's an error processing the token
     */
    public String getRoleFromToken(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        
        // Check if Authorization header is missing
        if (authHeader == null) {
            throw new MissingTokenException("Authorization header is required");
        }
        
        // Check if Authorization header has correct format
        if (!authHeader.startsWith("Bearer ")) {
            throw new InvalidTokenException("Authorization header must start with 'Bearer '");
        }

        final String jwt = authHeader.substring(7);
        
        // Check if token is empty after removing "Bearer "
        if (jwt.trim().isEmpty()) {
            throw new InvalidTokenException("Token cannot be empty");
        }
        
        try {
            // Extract role from JWT claims
            String role = jwtTokenService.extractClaim(jwt, claims -> claims.get("role", String.class));
            
            // Check if role is present in token
            if (role == null || role.trim().isEmpty()) {
                throw new InvalidTokenException("Role not found in token");
            }
            
            return role;
        } catch (Exception e) {
            // Check if it's a token expiration issue
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("expired")) {
                throw new ExpiredTokenException("Token has expired");
            }
            throw new TokenException("Failed to process token: " + e.getMessage());
        }
    }
}
