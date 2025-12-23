package com.example.smarthealth.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.stereotype.Service;

import com.example.smarthealth.dto.gamification.UnlockedAchievementDto;
import com.example.smarthealth.enums.AchievementRuleType;
import com.example.smarthealth.model.gamification.AchievementDefinition;
import com.example.smarthealth.model.gamification.UserAchievement;
import com.example.smarthealth.model.health.StepDaily;
import com.example.smarthealth.repository.AchievementDefinitionRepository;
import com.example.smarthealth.repository.StepDailyRepository;
import com.example.smarthealth.repository.UserAchievementRepository;
import com.example.smarthealth.repository.WorkoutSessionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementDefinitionRepository defRepo;
    private final UserAchievementRepository uaRepo;
    private final StepDailyRepository stepRepo;
    private final WorkoutSessionRepository workoutRepo;

    private final ObjectMapper om = new ObjectMapper();

    /** FR-AC-02, FR-AC-03, FR-AC-05: evaluate & return newly unlocked for popup */
    @Transactional
    public List<UnlockedAchievementDto> evaluateForUser(Long userId) {
        List<AchievementDefinition> defs = defRepo.findAll();
        List<UnlockedAchievementDto> newly = new ArrayList<>();

        for (AchievementDefinition def : defs) {
            if (uaRepo.existsByUserIdAndAchievement_Id(userId, def.getId()))
                continue;

            AchievementRuleType type = AchievementRuleType.valueOf(def.getRuleType());

            boolean ok = switch (type) {
                case STEPS_DAILY_MINIMUM -> stepsDailyMinimum(userId, def.getRuleConfig());
                case STEPS_DAILY_CONSECUTIVE_DAYS -> stepsConsecutive(userId, def.getRuleConfig());
                case TOTAL_STEPS_LAST_DAYS -> totalStepsLastDays(userId, def.getRuleConfig());
                case WORKOUT_COUNT_TOTAL -> workoutCountTotal(userId, def.getRuleConfig());
                case WORKOUTS_IN_WEEK -> workoutsInWeek(userId, def.getRuleConfig());
                default -> false;
            };

            if (ok) {
                UserAchievement ua = new UserAchievement();
                ua.setUserId(userId);
                ua.setAchievement(def);
                ua.setUnlockedAt(LocalDateTime.now());
                uaRepo.save(ua);

                newly.add(new UnlockedAchievementDto(
                        def.getCode(), def.getName(), def.getDescription(), def.getIconUrl(), ua.getUnlockedAt()));
            }
        }
        return newly;
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private boolean stepsDailyMinimum(Long userId, String cfg) {
        try {
            JsonNode n = om.readTree(cfg);
            int daily = n.get("dailySteps").asInt();

            LocalDate today = LocalDate.now();

            int steps = stepRepo.findByUser_IdAndDate(userId, today)
                    .map(StepDaily::getTotalSteps)
                    .orElse(0);

            return steps >= daily;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean stepsConsecutive(Long userId, String cfg) {
        try {
            JsonNode n = om.readTree(cfg);
            int daily = n.get("dailySteps").asInt();
            int days = n.get("consecutiveDays").asInt();

            LocalDate today = LocalDate.now();
            for (int i = 0; i < days; i++) {
                LocalDate d = today.minusDays(i);
                int steps = stepRepo.findByUser_IdAndDate(userId, d)
                        .map(StepDaily::getTotalSteps).orElse(0);
                if (steps < daily)
                    return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean totalStepsLastDays(Long userId, String cfg) {
        try {
            JsonNode n = om.readTree(cfg);
            long target = n.get("target").asLong();
            int lastDays = n.get("lastDays").asInt();

            LocalDate to = LocalDate.now();
            LocalDate from = to.minusDays(lastDays - 1L);
            long sum = stepRepo.sumStepsBetween(userId, from, to);
            return sum >= target;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean workoutCountTotal(Long userId, String cfg) {
        try {
            long count = om.readTree(cfg).get("count").asLong();
            long total = workoutRepo.countTotalWorkouts(userId);
            return total >= count;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean workoutsInWeek(Long userId, String cfg) {
        try {
            long count = om.readTree(cfg).get("count").asLong();
            LocalDate today = LocalDate.now();
            LocalDate monday = today.with(DayOfWeek.MONDAY);
            LocalDateTime from = monday.atStartOfDay();
            LocalDateTime to = LocalDateTime.now();
            long c = workoutRepo.countWorkoutsBetween(userId, from, to);
            return c >= count;
        } catch (Exception e) {
            return false;
        }
    }
}
