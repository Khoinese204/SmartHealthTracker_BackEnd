package com.example.smarthealth.dto.gamification;

import java.time.LocalDateTime;

public record AchievementListItemDto(
        String code,
        String name,
        String description,
        String iconUrl,
        boolean unlocked,
        LocalDateTime unlockedAt) {
}