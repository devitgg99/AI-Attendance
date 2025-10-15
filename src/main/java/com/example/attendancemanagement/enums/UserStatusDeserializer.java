package com.example.attendancemanagement.enums;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class UserStatusDeserializer extends JsonDeserializer<UserStatus> {
    
    @Override
    public UserStatus deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        if (value == null) {
            return UserStatus.ACTIVE; // default
        }
        
        switch (value.toLowerCase()) {
            case "active":
                return UserStatus.ACTIVE;
            case "deactivate":
                return UserStatus.DEACTIVATE;
            case "graduate":
                return UserStatus.GRADUATE;
            default:
                return UserStatus.ACTIVE; // default fallback
        }
    }
}








