package com.example.attendancemanagement.exception;

public class InvalidTokenException extends ApiException {
    public InvalidTokenException() {
        super("INVALID_TOKEN", "Invalid or malformed token", 401);
    }
    
    public InvalidTokenException(String message) {
        super("INVALID_TOKEN", message, 401);
    }
}
