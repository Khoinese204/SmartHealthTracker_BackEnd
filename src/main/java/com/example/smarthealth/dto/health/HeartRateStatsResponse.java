package com.example.smarthealth.dto.health;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HeartRateStatsResponse {
    private Integer minBpm;
    private Integer maxBpm;
    private Double avgBpm;
    private Long totalMeasurements;
}