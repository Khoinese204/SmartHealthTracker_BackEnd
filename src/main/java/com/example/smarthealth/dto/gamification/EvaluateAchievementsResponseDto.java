package com.example.smarthealth.dto.gamification;

import java.util.List;

public record EvaluateAchievementsResponseDto(
        int newUnlockedCount,
        List<UnlockedAchievementDto> newUnlocked) {
}