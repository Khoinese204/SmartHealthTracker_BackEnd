package com.example.smarthealth.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.smarthealth.config.CurrentUserService;
import com.example.smarthealth.dto.gamification.ChallengeDto;
import com.example.smarthealth.dto.gamification.ChallengeProgressDto;
import com.example.smarthealth.service.ChallengeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/gamification/challenges")
@RequiredArgsConstructor
public class ChallengesController {

    private final CurrentUserService currentUserService;
    private final ChallengeService challengeService;

    @Operation(summary = "List available challenges", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public List<ChallengeDto> available(@RequestParam(defaultValue = "true") boolean activeOnly) {
        return challengeService.listAvailable(activeOnly);
    }

    @Operation(summary = "User joins a challenges", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/{challengeId}/join")
    public ChallengeProgressDto join(@PathVariable Long challengeId) {
        Long userId = currentUserService.getCurrentUser().getId();
        return challengeService.join(userId, challengeId);
    }

    @Operation(summary = "List joined challenges of login user", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/me")
    public List<ChallengeProgressDto> myChallenges() {
        Long userId = currentUserService.getCurrentUser().getId();
        return challengeService.listMyChallenges(userId);
    }

    @Operation(summary = "Refresh one participation (if mobile wants) of login user", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/me/participations/{participationId}/refresh")
    public ChallengeProgressDto refresh(@PathVariable Long participationId) {
        Long userId = currentUserService.getCurrentUser().getId();
        return challengeService.refreshMyProgress(userId, participationId);
    }
}