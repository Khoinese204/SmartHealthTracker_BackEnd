package com.example.smarthealth.controller;

import org.springframework.web.bind.annotation.*;

import com.example.smarthealth.dto.gamification.LeaderboardResponseDto;
import com.example.smarthealth.service.LeaderboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/gamification/leaderboards")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService service;

    @Operation(summary = "Get leaderboard for steps", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/steps")
    public LeaderboardResponseDto steps(
            @RequestParam(defaultValue = "weekly") String range,
            @RequestParam(defaultValue = "GLOBAL") String scope,
            @RequestParam(required = false) Long groupId,
            @RequestParam(defaultValue = "50") int limit) {
        if ("GROUP".equalsIgnoreCase(scope) && groupId == null) {
            throw new IllegalArgumentException("groupId is required when scope=GROUP");
        }
        return service.steps(range, scope, groupId, limit);
    }
}
