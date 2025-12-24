package com.example.smarthealth.dto.health;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "Workout data submitted by authenticated user")
public class WorkoutRequest {

    @Schema(example = "RUNNING", description = "Workout type (RUNNING, WALKING, CYCLING, etc.)")
    private String type;

    @Schema(example = "2025-01-01T06:30:00", description = "Workout start time (ISO-8601)")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(example = "2025-01-01T07:00:00", description = "Workout end time (ISO-8601)")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(example = "1800", description = "Workout duration in seconds")
    private Integer durationSeconds;

    @Schema(example = "5000", description = "Distance in meters")
    private BigDecimal distanceMeters;

    @Schema(example = "2.78", description = "Average speed (meters per second)")
    private BigDecimal avgSpeedMps;

    @Schema(example = "360", description = "Average pace (seconds per kilometer)")
    private Integer avgPaceSecPerKm;

    @Schema(example = "320", description = "Calories burned")
    private Integer calories;

    @Schema(description = "Optional GPS points recorded during workout")
    private List<GpsPointDto> gpsPoints;
}