package com.example.smarthealth.dto.gamification;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ChallengeDto(
        Long id,
        String code,
        String name,
        String description,
        String metricType,
        BigDecimal targetValue,
        LocalDate startDate,
        LocalDate endDate,
        boolean global,
        Long groupId) {
}