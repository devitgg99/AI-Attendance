package com.example.attendancemanagement.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenService jwtTokenService, UserDetailsService userDetailsService) {
        this.jwtTokenService = jwtTokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        
        // If no Authorization header, let it pass through to be handled by AuthenticationEntryPoint
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        
        // Check if token is empty after removing "Bearer "
        if (jwt.trim().isEmpty()) {
            sendErrorResponse(response, "INVALID_TOKEN", "Token cannot be empty", "The provided token is empty");
            return;
        }
        
        final String username;
        try {
            username = jwtTokenService.extractUsername(jwt);
        } catch (Exception e) {
            // Handle different types of token errors
            String errorCode = "INVALID_TOKEN";
            String errorMessage = "Invalid token";
            String errorDetails = "The provided token is invalid or malformed";
            
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("expired")) {
                errorCode = "EXPIRED_TOKEN";
                errorMessage = "Token has expired";
                errorDetails = "Please refresh your token or login again";
            }
            
            sendErrorResponse(response, errorCode, errorMessage, errorDetails);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (Exception e) {
                sendErrorResponse(response, "USER_NOT_FOUND", "User not found", "The user associated with this token was not found");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
    
    private void sendErrorResponse(HttpServletResponse response, String code, String message, String details) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("code", code);
        body.put("message", message);
        body.put("details", details);
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}


