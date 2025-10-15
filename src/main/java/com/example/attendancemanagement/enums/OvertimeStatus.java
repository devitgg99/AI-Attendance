package com.example.attendancemanagement.enums;

/**
 * Enum for overtime request status
 */
public enum OvertimeStatus {
    PENDING("Pending", "Overtime request is pending approval"),
    APPROVED("Approved", "Overtime request has been approved"),
    REJECTED("Rejected", "Overtime request has been rejected");
    
    private final String displayName;
    private final String description;
    
    OvertimeStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
}





