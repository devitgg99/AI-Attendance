package com.example.attendancemanagement.service;

import com.example.attendancemanagement.dto.AuthDtos.CheckInRequest;
import com.example.attendancemanagement.dto.AuthDtos.CheckInResponse;
import com.example.attendancemanagement.entity.Attendance;
import com.example.attendancemanagement.entity.User;
import com.example.attendancemanagement.enums.CheckInStatus;
import com.example.attendancemanagement.exception.BadRequestException;
import com.example.attendancemanagement.repository.AttendanceRepository;
import com.example.attendancemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    
    private static final LocalTime CHECK_IN_DEADLINE = LocalTime.of(8, 1); // 8:01 AM
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public CheckInResponse checkIn(UUID userId) {
        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();
        LocalTime check_time = LocalTime.now();

        System.out.println("currentTime "+ currentTime);
        System.out.println("check_time "+ check_time);

        // Check if already checked in today
        Optional<Attendance> existingAttendance = attendanceRepository
                .findByUserUserIdAndCheckInDate(userId, today);

        if (existingAttendance.isPresent()) {
            throw new BadRequestException("You have already checked in today");
        }

        // Determine check-in status based on time
        CheckInStatus checkinStatus = determineCheckInStatus(currentTime);


        // Create attendance record
        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setCheckIn(currentTime);
        attendance.setCheckinStatus(checkinStatus);

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

    private CheckInStatus determineCheckInStatus(LocalTime currentTime) {
        if (currentTime.isBefore(CHECK_IN_DEADLINE)) {
            return CheckInStatus.CHECKIN; // On time
        } else {
            return CheckInStatus.CHECKIN_LATE; // Late (after 8:01 AM)
        }
    }

    private String generateCheckInMessage(CheckInStatus checkinStatus, LocalTime currentTime) {
        if (checkinStatus == CheckInStatus.CHECKIN_LATE) {
            return "You checked in late at " + currentTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        } else {
            return "You checked in on time at " + currentTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        }
    }
}