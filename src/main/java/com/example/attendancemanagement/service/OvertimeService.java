package com.example.attendancemanagement.service;

import com.example.attendancemanagement.dto.OvertimeDtos.RequestOvertime;
import com.example.attendancemanagement.dto.OvertimeDtos.OvertimeResponse;
import com.example.attendancemanagement.entity.Overtime;
import com.example.attendancemanagement.entity.User;
import com.example.attendancemanagement.enums.OvertimeStatus;
import com.example.attendancemanagement.exception.BadRequestException;
import com.example.attendancemanagement.repository.OvertimeRepository;
import com.example.attendancemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OvertimeService {

	private final OvertimeRepository overtimeRepository;
	private final UserRepository userRepository;

	public OvertimeResponse requestOvertime(UUID userId, RequestOvertime dto) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new BadRequestException("User not found"));

		LocalDate requestDate;
		LocalTime startTime;
		LocalTime endTime;
		try {
			requestDate = LocalDate.parse(dto.getRequestDate());
			startTime = LocalTime.parse(dto.getStartTime());
			endTime = LocalTime.parse(dto.getEndTime());
		} catch (Exception e) {
			throw new BadRequestException("Invalid date/time format. Use yyyy-MM-dd and HH:mm:ss");
		}

		// Validate request date is not in the past
		LocalDate today = LocalDate.now();
		if (requestDate.isBefore(today)) {
			throw new BadRequestException("Overtime request date cannot be in the past");
		}

		if (endTime.isBefore(startTime)) {
			throw new BadRequestException("end_time must be after start_time");
		}

		// Calculate actual duration from start and end times
		long minutesBetween = java.time.Duration.between(startTime, endTime).toMinutes();
		BigDecimal calculatedDuration = BigDecimal.valueOf(minutesBetween).divide(BigDecimal.valueOf(60), 2, java.math.RoundingMode.HALF_UP);
		
		// Validate minimum duration of 2 hours (1h:59min counts as 2 hours)
		if (calculatedDuration.compareTo(BigDecimal.valueOf(1.99)) < 0) {
			throw new BadRequestException("Duration must be at least 2 hours (calculated: " + calculatedDuration + " hours)");
		}
		
		// Validate provided duration matches calculated duration (allow 1 minute tolerance)
		BigDecimal providedDuration = dto.getDuration();
		if (providedDuration == null) {
			throw new BadRequestException("Duration is required");
		}
		
		BigDecimal tolerance = BigDecimal.valueOf(0.02); // 1 minute tolerance
		BigDecimal difference = providedDuration.subtract(calculatedDuration).abs();
		
		if (difference.compareTo(tolerance) > 0) {
			throw new BadRequestException("Provided duration (" + providedDuration + " hours) does not match calculated duration (" + calculatedDuration + " hours) from start/end times");
		}

		Overtime overtime = new Overtime();
		overtime.setUser(user);
		overtime.setRequestDate(requestDate);
		overtime.setStartTime(startTime);
		overtime.setEndTime(endTime);
		overtime.setObjective(dto.getObjective());
		overtime.setDuration(dto.getDuration() != null ? dto.getDuration() : BigDecimal.ZERO);
		overtime.setStatus(OvertimeStatus.PENDING);
		// calculate weekday/weekend automatically from request date
		overtime.setCalculatedFields();

		Overtime saved = overtimeRepository.save(overtime);

		OvertimeResponse res = new OvertimeResponse();
		res.setOvertimeId(saved.getOvertimeId().toString());
		res.setUserId(userId.toString());
		res.setRequestDate(saved.getRequestDate().toString());
		res.setStartTime(saved.getStartTime().toString());
		res.setEndTime(saved.getEndTime().toString());
		res.setDuration(saved.getDuration().setScale(2, java.math.RoundingMode.HALF_UP).toPlainString());
		res.setIsWeekday(saved.getIsWeekday());
		res.setObjective(saved.getObjective());
		res.setStatus(saved.getStatus().name());
		return res;
	}
}

