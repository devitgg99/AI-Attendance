package com.example.attendancemanagement.exception;

public class ExpiredTokenException extends ApiException {
    public ExpiredTokenException() {
        super("EXPIRED_TOKEN", "Token has expired", 401);
    }
    
    public ExpiredTokenException(String message) {
        super("EXPIRED_TOKEN", message, 401);
    }
}
