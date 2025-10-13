package com.example.attendancemanagement.service;

import com.example.attendancemanagement.dto.AuthDtos.CheckInResponse;
import com.example.attendancemanagement.dto.AuthDtos.CheckOutResponse;
import com.example.attendancemanagement.dto.AuthDtos.AttendanceStatusResponse;
import com.example.attendancemanagement.dto.AuthDtos.AttendanceRecord;
import com.example.attendancemanagement.enums.AttendanceStatus;
import com.example.attendancemanagement.entity.Attendance;
import com.example.attendancemanagement.entity.User;
import com.example.attendancemanagement.enums.CheckInStatus;
import com.example.attendancemanagement.enums.CheckOutStatus;
import com.example.attendancemanagement.enums.DateStatus;
import com.example.attendancemanagement.enums.OvertimeStatus;
import com.example.attendancemanagement.exception.BadRequestException;
import com.example.attendancemanagement.repository.AttendanceRepository;
import com.example.attendancemanagement.repository.OvertimeRepository;
import com.example.attendancemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final OvertimeRepository overtimeRepository;
    
    private static final LocalTime CHECK_IN_DEADLINE = LocalTime.of(8, 1); // 8:01 AM
    private static final LocalTime CHECK_OUT_DEADLINE = LocalTime.of(17, 0); // 5:00 PM
    private static final LocalTime EARLIEST_CHECKIN_TIME = LocalTime.of(6, 0); // 6:00 AM
    private static final LocalTime OVERTIME_CHECKIN_TIME = LocalTime.of(17, 0); // 5:00 PM
    private static final LocalTime MISSED_CHECKOUT_TIME = LocalTime.of(18, 0); // 6:00 PM
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final ZoneId JAKARTA_ZONE = ZoneId.of("Asia/Jakarta");

    public CheckInResponse checkIn(UUID userId) {
        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        // Get current time in Jakarta timezone
        ZonedDateTime jakartaNow = ZonedDateTime.now(JAKARTA_ZONE);
        LocalDate today = jakartaNow.toLocalDate();
        LocalDateTime now = jakartaNow.toLocalDateTime();
        LocalTime currentTime = jakartaNow.toLocalTime().truncatedTo(ChronoUnit.SECONDS);

        System.out.println("Jakarta time: " + jakartaNow);
        System.out.println("Today: " + today);
        System.out.println("Current time (truncated): " + currentTime);

        // Check if already checked in today
        Optional<Attendance> existingAttendance = attendanceRepository
                .findByUserUserIdAndAttendanceDate(userId, today);

        if (existingAttendance.isPresent()) {
            throw new BadRequestException("You have already checked in today");
        }

        // Validate check-in time restrictions
        validateCheckInTime(userId, currentTime, today);

        // Determine check-in status based on time
        CheckInStatus checkinStatus = determineCheckInStatus(currentTime);
        
        // Determine date status based on current date
        DateStatus dateStatus = determineDateStatus(today);

        // Create attendance record
        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setCheckIn(currentTime);
        attendance.setCheckinStatus(checkinStatus);
        attendance.setDateStatus(dateStatus);

        Attendance savedAttendance = attendanceRepository.save(attendance);

        // Create response
        CheckInResponse response = new CheckInResponse();
        response.setAttendanceId(savedAttendance.getAttendanceId().toString());
        response.setUserId(userId.toString());
        response.setAttendanceDate(today.format(DATE_FORMATTER));
        response.setCheckInTime(now.format(TIME_FORMATTER));
        response.setCheckInStatus(checkinStatus.name());
        
        // Set appropriate message
        String message = generateCheckInMessage(checkinStatus, currentTime);
        response.setMessage(message);

        return response;
    }

    public CheckOutResponse checkOut(UUID userId) {
        // Validate user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        // Get current time in Jakarta timezone
        ZonedDateTime jakartaNow = ZonedDateTime.now(JAKARTA_ZONE);
        LocalDate today = jakartaNow.toLocalDate();
        LocalTime currentTime = jakartaNow.toLocalTime().truncatedTo(ChronoUnit.SECONDS);

        System.out.println("Checkout - Jakarta time: " + jakartaNow);
        System.out.println("Checkout - Today: " + today);
        System.out.println("Checkout - Current time: " + currentTime);

        // Find today's attendance record
        Attendance attendance = attendanceRepository
                .findByUserUserIdAndAttendanceDate(userId, today)
                .orElseThrow(() -> new BadRequestException("You must check in first before checking out"));

        // Check if already checked out
        if (attendance.getCheckoutOut() != null) {
            throw new BadRequestException("You have already checked out today");
        }

        // Validate checkout time restrictions
        validateCheckOutTime(userId, currentTime, today);

        // Determine checkout status based on date status
        DateStatus dateStatus = attendance.getDateStatus();
        CheckOutStatus checkoutStatus = determineCheckOutStatus(dateStatus, currentTime);

        // Update attendance record
        attendance.setCheckoutOut(currentTime);
        attendance.setCheckoutStatus(checkoutStatus);

        Attendance savedAttendance = attendanceRepository.save(attendance);

        // Create response
        CheckOutResponse response = new CheckOutResponse();
        response.setAttendanceId(savedAttendance.getAttendanceId().toString());
        response.setUserId(userId.toString());
        response.setAttendanceDate(today.format(DATE_FORMATTER));
        response.setCheckInTime(savedAttendance.getCheckIn().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        response.setCheckOutTime(currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        response.setCheckInStatus(savedAttendance.getCheckinStatus().name());
        response.setCheckOutStatus(checkoutStatus.name());
        response.setDateStatus(dateStatus.name());
        
        // Set appropriate message
        String message = generateCheckOutMessage(checkoutStatus, currentTime, dateStatus);
        response.setMessage(message);

        return response;
    }

    private CheckInStatus determineCheckInStatus(LocalTime currentTime) {
        if (currentTime.isBefore(CHECK_IN_DEADLINE)) {
            return CheckInStatus.CHECKIN; // On time
        } else {
            return CheckInStatus.CHECKIN_LATE; // Late (after 8:01 AM)
        }
    }

    private DateStatus determineDateStatus(LocalDate date) {
        java.time.DayOfWeek dayOfWeek = date.getDayOfWeek();
        
        // Check if it's weekend (Saturday or Sunday)
        if (dayOfWeek == java.time.DayOfWeek.SATURDAY || dayOfWeek == java.time.DayOfWeek.SUNDAY) {
            return DateStatus.WEEKEND;
        }
        
        // For now, we'll consider weekdays as regular workdays
        // You can add more logic here for overtime detection if needed
        // For example, check if it's a holiday, or if it's after certain hours
        return DateStatus.WEEKDAY;
    }

    private CheckOutStatus determineCheckOutStatus(DateStatus dateStatus, LocalTime currentTime) {
        // If checkout is after 6:00 PM, it's considered missed checkout
        if (currentTime.isAfter(MISSED_CHECKOUT_TIME)) {
            return CheckOutStatus.MISSED_CHECKOUT;
        }
        
        switch (dateStatus) {
            case WEEKDAY:
                return CheckOutStatus.CHECKOUT; // Normal checkout on weekdays
            case WEEKEND:
                return CheckOutStatus.CHECKOUT; // Checkout on weekends
            case OVERTIME:
                return CheckOutStatus.CHECKOUT; // Checkout during overtime
            default:
                return CheckOutStatus.CHECKOUT;
        }
    }

    private String generateCheckInMessage(CheckInStatus checkinStatus, LocalTime currentTime) {
        if (checkinStatus == CheckInStatus.CHECKIN_LATE) {
            return "You checked in late at " + currentTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        } else {
            return "You checked in on time at " + currentTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        }
    }

    private String generateCheckOutMessage(CheckOutStatus checkoutStatus, LocalTime currentTime, DateStatus dateStatus) {
        String timeStr = currentTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        String dateType = dateStatus.name().toLowerCase();
        
        switch (checkoutStatus) {
            case CHECKOUT:
                return "You checked out successfully at " + timeStr + " on " + dateType;
            case PERMISSION:
                return "You checked out with permission at " + timeStr + " on " + dateType;
            case MISSED_CHECKOUT:
                return "You missed checkout on " + dateType;
            default:
                return "Checkout completed at " + timeStr;
        }
    }

    private void validateCheckInTime(UUID userId, LocalTime currentTime, LocalDate today) {
        // Rule 1: Check-in is only allowed from 6:00 AM onwards
        if (currentTime.isBefore(EARLIEST_CHECKIN_TIME)) {
            throw new BadRequestException("Check-in is only allowed from 6:00 AM onwards");
        }

        // Rule 2: If checking in after 5:00 PM, must have approved overtime request
        if (currentTime.isAfter(OVERTIME_CHECKIN_TIME) || currentTime.equals(OVERTIME_CHECKIN_TIME)) {
            // Check if user has approved overtime request for today
            Optional<com.example.attendancemanagement.entity.Overtime> approvedOvertime = 
                overtimeRepository.findByUserUserIdAndRequestDate(userId, today);
            
            if (approvedOvertime.isEmpty()) {
                throw new BadRequestException("Check-in after 5:00 PM requires an approved overtime request for today");
            }
            
            if (approvedOvertime.get().getStatus() != OvertimeStatus.APPROVED) {
                throw new BadRequestException("Check-in after 5:00 PM requires an approved overtime request. Current status: " + 
                    approvedOvertime.get().getStatus().getDisplayName());
            }
        }
    }

    private void validateCheckOutTime(UUID userId, LocalTime currentTime, LocalDate today) {
        // Rule 1: Regular checkout must be after 5:00 PM
        if (currentTime.isBefore(CHECK_OUT_DEADLINE)) {
            throw new BadRequestException("You cannot check out before 5:00 PM");
        }

        // Rule 2: If checking out after 6:00 PM, must have approved overtime request
        if (currentTime.isAfter(MISSED_CHECKOUT_TIME) || currentTime.equals(MISSED_CHECKOUT_TIME)) {
            // Check if user has approved overtime request for today
            Optional<com.example.attendancemanagement.entity.Overtime> approvedOvertime = 
                overtimeRepository.findByUserUserIdAndRequestDate(userId, today);
            
            if (approvedOvertime.isEmpty()) {
                throw new BadRequestException("Check-out after 6:00 PM requires an approved overtime request for today");
            }
            
            if (approvedOvertime.get().getStatus() != OvertimeStatus.APPROVED) {
                throw new BadRequestException("Check-out after 6:00 PM requires an approved overtime request. Current status: " + 
                    approvedOvertime.get().getStatus().getDisplayName());
            }

            // Rule 3: Cannot check out before the end time of approved overtime request
            LocalTime overtimeEndTime = approvedOvertime.get().getEndTime();
            if (currentTime.isBefore(overtimeEndTime)) {
                throw new BadRequestException("You cannot check out before the end time of your approved overtime request (" + 
                    overtimeEndTime.format(DateTimeFormatter.ofPattern("HH:mm")) + ")");
            }
        }
    }


    public AttendanceStatusResponse getAttendanceHistory(UUID userId, AttendanceStatus status, String startDate, String endDate) {
        // Parse dates with default logic
        LocalDate start;
        LocalDate end;
        
        if (startDate != null && endDate != null) {
            // User provided both dates
            try {
                start = LocalDate.parse(startDate);
                end = LocalDate.parse(endDate);
            } catch (Exception e) {
                throw new BadRequestException("Invalid date format. Please use dd-MM-yyyy format.");
            }
        } else {
            // Use default: first of current month to end of current month
            LocalDate now = LocalDate.now();
            start = now.withDayOfMonth(1); // First day of current month
            end = now.withDayOfMonth(now.lengthOfMonth()); // Last day of current month
        }
        
        // Get attendance records based on status filter for the specific user
        List<Attendance> attendanceList;
        if (status != null) {
            // Filter by specific status and user
            attendanceList = getAttendanceByStatusAndUser(userId, status, start, end);
        } else {
            // Get all attendance records in the date range for the specific user
            attendanceList = attendanceRepository.findByUserUserIdAndAttendanceDateBetween(userId, start, end);
        }
        
        // Convert to DTOs
        List<AttendanceRecord> attendanceRecords = attendanceList.stream()
            .map(this::convertToAttendanceRecord)
            .collect(Collectors.toList());
        
        AttendanceStatusResponse response = new AttendanceStatusResponse();
        response.setAttendanceRecords(attendanceRecords);
        
        return response;
    }
    
    private List<Attendance> getAttendanceByStatusAndUser(UUID userId, AttendanceStatus status, LocalDate startDate, LocalDate endDate) {
        switch (status) {
            case MISSED_CHECKIN:
                return attendanceRepository.findMissedCheckInRecordsByUser(userId, startDate, endDate);
            case CHECKIN_LATE:
                return attendanceRepository.findLateCheckInRecordsByUser(userId, startDate, endDate);
            case ABSENT:
                return attendanceRepository.findAbsentRecordsByUser(userId, startDate, endDate);
            case MISSED_CHECKOUT:
                return attendanceRepository.findMissedCheckOutRecordsByUser(userId, startDate, endDate);
            case PRESENT:
                return attendanceRepository.findPresentRecordsByUser(userId, startDate, endDate);
            default:
                return attendanceRepository.findByUserUserIdAndAttendanceDateBetween(userId, startDate, endDate);
        }
    }

    public AttendanceStatusResponse getAttendanceByDate(UUID userId, String date) {
        // Parse date or use current date as default
        LocalDate targetDate;
        if (date != null && !date.trim().isEmpty()) {
            try {
                targetDate = LocalDate.parse(date);
            } catch (Exception e) {
                throw new BadRequestException("Invalid date format. Please use dd-MM-yyyy format.");
            }
        } else {
            // Use current date as default
            targetDate = LocalDate.now();
        }
        
        // Get attendance record for the specific user and date
        Optional<Attendance> attendance = attendanceRepository.findByUserUserIdAndAttendanceDate(userId, targetDate);
        
        // Convert to DTOs
        List<AttendanceRecord> attendanceRecords = attendance
            .map(this::convertToAttendanceRecord)
            .map(List::of)
            .orElse(List.of());
        
        AttendanceStatusResponse response = new AttendanceStatusResponse();
        response.setAttendanceRecords(attendanceRecords);
        
        return response;
    }
    
    
    private AttendanceRecord convertToAttendanceRecord(Attendance attendance) {
        AttendanceRecord record = new AttendanceRecord();
        record.setAttendanceId(attendance.getAttendanceId().toString());
        record.setAttendanceDate(attendance.getCreatedAt().toLocalDate().format(DATE_FORMATTER));
        
        if (attendance.getCheckIn() != null) {
            record.setCheckInTime(attendance.getCheckIn().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            // Create full datetime for check-in
            LocalDateTime checkInDateTime = attendance.getCreatedAt().toLocalDate().atTime(attendance.getCheckIn());
            record.setCheckInDateTime(checkInDateTime.format(TIME_FORMATTER));
        }
        
        if (attendance.getCheckoutOut() != null) {
            record.setCheckOutTime(attendance.getCheckoutOut().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            // Create full datetime for check-out
            LocalDateTime checkOutDateTime = attendance.getCreatedAt().toLocalDate().atTime(attendance.getCheckoutOut());
            record.setCheckOutDateTime(checkOutDateTime.format(TIME_FORMATTER));
        }
        
        if (attendance.getCheckinStatus() != null) {
            record.setCheckInStatus(attendance.getCheckinStatus().name());
        }
        
        if (attendance.getCheckoutStatus() != null) {
            record.setCheckOutStatus(attendance.getCheckoutStatus().name());
        }
        
        if (attendance.getDateStatus() != null) {
            record.setDateStatus(attendance.getDateStatus().name());
        }
        
        return record;
    }
    
}