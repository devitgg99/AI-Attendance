package com.example.attendancemanagement.exception;

public class UnauthorizedException extends ApiException {
    public UnauthorizedException(String message) {
        super("UNAUTHORIZED", message, 401);
    }
}




