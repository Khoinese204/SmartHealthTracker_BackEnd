package com.example.smarthealth.dto.gamification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ChallengeProgressDto(
        Long participationId,
        ChallengeDto challenge,
        LocalDateTime joinedAt,
        BigDecimal currentValue,
        BigDecimal completionPercentage,
        boolean completed,
        LocalDateTime completedAt) {
}