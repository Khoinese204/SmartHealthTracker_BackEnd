package com.example.smarthealth.dto.gamification;

import java.time.LocalDate;
import java.util.List;

public record LeaderboardResponseDto(
        String metric, // "STEPS"
        String range, // "WEEKLY"|"MONTHLY" or custom
        String scope, // "GLOBAL"|"GROUP"
        Long groupId,
        LocalDate from,
        LocalDate to,
        List<LeaderboardRowDto> rows) {
}