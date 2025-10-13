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

		if (endTime.isBefore(startTime)) {
			throw new BadRequestException("end_time must be after start_time");
		}

		// Validate minimum duration of 2 hours
		if (dto.getDuration() == null || dto.getDuration().compareTo(BigDecimal.valueOf(2)) < 0) {
			throw new BadRequestException("Duration must be at least 2 hours");
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

