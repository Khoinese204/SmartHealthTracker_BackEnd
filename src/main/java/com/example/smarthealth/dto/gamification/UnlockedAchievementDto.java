package com.example.smarthealth.dto.gamification;

import java.time.LocalDateTime;

public record UnlockedAchievementDto(
        String code,
        String name,
        String description,
        String iconUrl,
        LocalDateTime unlockedAt) {
}
