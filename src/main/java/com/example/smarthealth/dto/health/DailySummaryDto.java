package com.example.smarthealth.dto.health;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class DailySummaryDto {
    private LocalDate date;

    private Integer totalSteps;
    private Double distanceKm;
    private Double kcalBurned;

    private Integer sleepDurationMinutes;
    private String sleepQuality;

    private Integer workoutCount;
    private Integer workoutDurationMinutes;

    private Integer avgHeartRate;
    private Integer maxHeartRate;
    private Integer minHeartRate;

    private Integer healthScore; 
}