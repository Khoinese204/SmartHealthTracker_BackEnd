package com.example.smarthealth.dto.health;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class WorkoutRequest {
    private String type; 

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    private Integer durationSeconds;
    private BigDecimal distanceMeters;
    private BigDecimal avgSpeedMps;
    private Integer avgPaceSecPerKm;
    private Integer calories;

    private List<GpsPointDto> gpsPoints;
}