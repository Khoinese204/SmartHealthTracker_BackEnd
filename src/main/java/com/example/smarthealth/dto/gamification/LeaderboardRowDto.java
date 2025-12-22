package com.example.smarthealth.dto.gamification;

public record LeaderboardRowDto(
        int rank,
        Long userId,
        String userName,
        long metricValue) {
}