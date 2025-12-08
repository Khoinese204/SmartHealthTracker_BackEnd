package com.example.smarthealth.dto.health;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class StepResponse {
    private LocalDate date;
    private Integer totalSteps;
    private Double kCalBurned;
    private Double distanceKm;
}