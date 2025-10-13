package com.example.attendancemanagement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OvertimeDtos {

	@Data
	public static class RequestOvertime {
		@JsonProperty("request_date")
		@NotBlank
		private String requestDate; // dd-MM-yyyy
		@JsonProperty("start_time")
		@NotBlank
		private String startTime; // HH:mm:ss
		@JsonProperty("end_time")
		@NotBlank
		private String endTime; // HH:mm:ss
		@JsonProperty("duration")
		@NotNull
		private java.math.BigDecimal duration; // hours (5,2)
		@JsonProperty("objective")
		private String objective;
	}

	@Data
	public static class OvertimeResponse {
		@JsonProperty("overtime_id")
		private String overtimeId;
		@JsonProperty("user_id")
		private String userId;
		@JsonProperty("request_date")
		private String requestDate;
		@JsonProperty("start_time")
		private String startTime;
		@JsonProperty("end_time")
		private String endTime;
		@JsonProperty("duration")
		private String duration;
		@JsonProperty("is_weekday")
		private Boolean isWeekday;
		@JsonProperty("objective")
		private String objective;
		@JsonProperty("status")
		private String status;
	}
}

