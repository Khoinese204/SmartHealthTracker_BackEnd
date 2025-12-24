package com.example.smarthealth.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.smarthealth.dto.gamification.ChallengeDto;
import com.example.smarthealth.dto.gamification.ChallengeProgressDto;
import com.example.smarthealth.model.gamification.Challenge;
import com.example.smarthealth.model.gamification.ChallengeParticipation;
import com.example.smarthealth.repository.ChallengeParticipationRepository;
import com.example.smarthealth.repository.ChallengeRepository;
import com.example.smarthealth.repository.StepDailyRepository;
import com.example.smarthealth.repository.WorkoutSessionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepo;
    private final ChallengeParticipationRepository partRepo;
    private final StepDailyRepository stepRepo;
    private final WorkoutSessionRepository workoutRepo;

    /** FR-CH-02 */
    @Transactional
    public ChallengeProgressDto join(Long userId, Long challengeId) {
        Challenge ch = challengeRepo.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("Challenge not found"));

        partRepo.findByChallenge_IdAndUserId(challengeId, userId).ifPresent(x -> {
            throw new IllegalStateException("Already joined this challenge");
        });

        ChallengeParticipation p = new ChallengeParticipation();
        p.setChallenge(ch);
        p.setUserId(userId);
        p.setJoinedAt(LocalDateTime.now());
        p.setCurrentValue(BigDecimal.ZERO);
        p.setCompletionPercentage(BigDecimal.ZERO);
        p.setCompleted(false);

        p = partRepo.save(p);
        p = refreshProgressInternal(p); // compute immediately
        return toProgressDto(p);
    }

    /** FR-CH-03: compute progress & completion */
    @Transactional
    public ChallengeProgressDto refreshMyProgress(Long userId, Long participationId) {
        ChallengeParticipation p = partRepo.findById(participationId)
                .orElseThrow(() -> new IllegalArgumentException("Participation not found"));

        if (!p.getUserId().equals(userId)) {
            throw new IllegalStateException("Forbidden");
        }

        p = refreshProgressInternal(p);
        return toProgressDto(p);
    }

    /** FR-CH-04: joined list with progress */
    @Transactional
    public List<ChallengeProgressDto> listMyChallenges(Long userId) {
        List<ChallengeParticipation> list = partRepo.findByUserIdOrderByJoinedAtDesc(userId);
        for (ChallengeParticipation p : list)
            refreshProgressInternal(p);
        return list.stream().map(this::toProgressDto).toList();
    }

    /** FR-CH-01: available challenges */
    public List<ChallengeDto> listAvailable(boolean activeOnly) {
        LocalDate today = LocalDate.now();
        List<Challenge> list = activeOnly ? challengeRepo.findByEndDateGreaterThanEqual(today)
                : challengeRepo.findAll();
        return list.stream().map(this::toChallengeDto).toList();
    }

    private ChallengeParticipation refreshProgressInternal(ChallengeParticipation p) {
        Challenge ch = p.getChallenge();
        LocalDate today = LocalDate.now();

        // if already completed, keep
        if (p.isCompleted())
            return p;

        // if challenge not started yet, keep 0
        if (today.isBefore(ch.getStartDate()))
            return p;

        // compute in window [start..min(today,end)]
        LocalDate fromDate = ch.getStartDate();
        LocalDate toDate = today.isAfter(ch.getEndDate()) ? ch.getEndDate() : today;

        BigDecimal current = computeMetric(ch.getMetricType(), p.getUserId(), fromDate, toDate);
        BigDecimal target = ch.getTargetValue();

        BigDecimal percent = BigDecimal.ZERO;
        if (target.compareTo(BigDecimal.ZERO) > 0) {
            percent = current.multiply(BigDecimal.valueOf(100))
                    .divide(target, 2, RoundingMode.HALF_UP)
                    .min(BigDecimal.valueOf(100));
        }

        p.setCurrentValue(current);
        p.setCompletionPercentage(percent);

        if (current.compareTo(target) >= 0) {
            p.setCompleted(true);
            p.setCompletedAt(LocalDateTime.now());
        }

        // if ended and not completed -> keep as not completed (fail state optional)
        return p;
    }

    private BigDecimal computeMetric(String metricType, Long userId, LocalDate from, LocalDate to) {
        String m = metricType == null ? "" : metricType.trim();
        return switch (m) {
            case "STEPS" -> BigDecimal.valueOf(stepRepo.sumStepsBetween(userId, from, to));
            case "WORKOUT_COUNT" -> {
                LocalDateTime f = from.atStartOfDay();
                LocalDateTime t = to.plusDays(1).atStartOfDay(); // exclusive
                yield BigDecimal.valueOf(workoutRepo.countWorkoutsBetween(userId, f, t));
            }
            default -> BigDecimal.ZERO;
        };
    }

    private ChallengeDto toChallengeDto(Challenge c) {
        return new ChallengeDto(
                c.getId(), c.getCode(), c.getName(), c.getDescription(),
                c.getMetricType(), c.getTargetValue(), c.getStartDate(), c.getEndDate(),
                c.isGlobal(), c.getGroupId());
    }

    private ChallengeProgressDto toProgressDto(ChallengeParticipation p) {
        return new ChallengeProgressDto(
                p.getId(),
                toChallengeDto(p.getChallenge()),
                p.getJoinedAt(),
                p.getCurrentValue(),
                p.getCompletionPercentage(),
                p.isCompleted(),
                p.getCompletedAt());
    }
}