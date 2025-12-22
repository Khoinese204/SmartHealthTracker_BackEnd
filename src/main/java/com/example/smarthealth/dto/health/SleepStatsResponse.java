package com.example.smarthealth.dto.health;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SleepStatsResponse {
    private Long totalSessions;     
    private Double avgDurationMinutes;
    private Long totalSleepMinutes;
}