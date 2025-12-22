package com.example.smarthealth.controller;

import java.util.*;

import org.springframework.web.bind.annotation.*;

import com.example.smarthealth.config.CurrentUserService;
import com.example.smarthealth.dto.gamification.AchievementListItemDto;
import com.example.smarthealth.dto.gamification.EvaluateAchievementsResponseDto;
import com.example.smarthealth.dto.gamification.UnlockedAchievementDto;
import com.example.smarthealth.model.gamification.AchievementDefinition;
import com.example.smarthealth.model.gamification.UserAchievement;
import com.example.smarthealth.repository.AchievementDefinitionRepository;
import com.example.smarthealth.repository.UserAchievementRepository;
import com.example.smarthealth.service.AchievementService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/gamification/achievements")
@RequiredArgsConstructor
public class AchievementsController {

    private final CurrentUserService currentUserService;
    private final AchievementService achievementService;
    private final AchievementDefinitionRepository defRepo;
    private final UserAchievementRepository uaRepo;

    @Operation(summary = "List achievements unlocked (and show locked too for UI grid) of login user", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/me")
    public List<AchievementListItemDto> myAchievements() {
        Long userId = currentUserService.getCurrentUser().getId();

        Map<Long, UserAchievement> unlockedMap = new HashMap<>();
        for (UserAchievement ua : uaRepo.findByUserIdOrderByUnlockedAtDesc(userId)) {
            unlockedMap.put(ua.getAchievement().getId(), ua);
        }

        List<AchievementDefinition> defs = defRepo.findAll();
        List<AchievementListItemDto> out = new ArrayList<>();
        for (AchievementDefinition def : defs) {
            UserAchievement ua = unlockedMap.get(def.getId());
            out.add(new AchievementListItemDto(
                    def.getCode(),
                    def.getName(),
                    def.getDescription(),
                    def.getIconUrl(),
                    ua != null,
                    ua != null ? ua.getUnlockedAt() : null));
        }
        return out;
    }

    @Operation(summary = "Event-based evaluate; return newly unlocked for popup of login user", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/me/evaluate")
    public EvaluateAchievementsResponseDto evaluateNow() {
        Long userId = currentUserService.getCurrentUser().getId();
        List<UnlockedAchievementDto> newly = achievementService.evaluateForUser(userId);
        return new EvaluateAchievementsResponseDto(newly.size(), newly);
    }
}