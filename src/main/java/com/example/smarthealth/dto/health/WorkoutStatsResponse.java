package com.example.smarthealth.dto.health;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutStatsResponse {
    private Long totalSessions;   
    private Long totalDurationSeconds;
    private BigDecimal totalDistanceMeters;
    private Long totalCalories;
}