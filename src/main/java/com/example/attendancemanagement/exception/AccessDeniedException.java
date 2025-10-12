package com.example.attendancemanagement.exception;

public class AccessDeniedException extends ApiException {
    public AccessDeniedException() {
        super("ACCESS_DENIED", "Access denied", 403);
    }
    
    public AccessDeniedException(String message) {
        super("ACCESS_DENIED", message, 403);
    }
}
