package com.example.attendancemanagement.exception;

public class MissingTokenException extends ApiException {
    public MissingTokenException() {
        super("MISSING_TOKEN", "Authorization token is required", 401);
    }
    
    public MissingTokenException(String message) {
        super("MISSING_TOKEN", message, 401);
    }
}
