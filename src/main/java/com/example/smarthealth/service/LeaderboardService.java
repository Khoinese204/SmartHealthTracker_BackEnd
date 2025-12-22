package com.example.smarthealth.service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.smarthealth.dto.gamification.LeaderboardResponseDto;
import com.example.smarthealth.dto.gamification.LeaderboardRowDto;
import com.example.smarthealth.repository.LeaderboardRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final LeaderboardRepository repo;

    public LeaderboardResponseDto steps(String range, String scope, Long groupId, int limit) {
        LocalDate today = LocalDate.now();
        LocalDate from = resolveFrom(range, today);
        LocalDate to = today;

        boolean isGroup = "GROUP".equalsIgnoreCase(scope);

        List<Object[]> raw = isGroup
                ? repo.groupSteps(groupId, from, to, limit)
                : repo.globalSteps(from, to, limit);

        List<LeaderboardRowDto> rows = new ArrayList<>();
        int rank = 1;
        for (Object[] r : raw) {
            Long userId = ((Number) r[0]).longValue();
            String name = (String) r[1];
            long value = ((Number) r[2]).longValue();
            rows.add(new LeaderboardRowDto(rank++, userId, name, value));
        }

        return new LeaderboardResponseDto(
                "STEPS",
                normalizeRange(range),
                isGroup ? "GROUP" : "GLOBAL",
                isGroup ? groupId : null,
                from,
                to,
                rows);
    }

    private LocalDate resolveFrom(String range, LocalDate today) {
        String r = range == null ? "weekly" : range.trim().toLowerCase();
        return switch (r) {
            case "monthly" -> today.withDayOfMonth(1);
            case "weekly" -> today.with(DayOfWeek.MONDAY);
            default -> today.with(DayOfWeek.MONDAY); // default weekly
        };
    }

    private String normalizeRange(String range) {
        String r = range == null ? "WEEKLY" : range.trim().toUpperCase();
        return switch (r) {
            case "MONTHLY" -> "MONTHLY";
            default -> "WEEKLY";
        };
    }
}