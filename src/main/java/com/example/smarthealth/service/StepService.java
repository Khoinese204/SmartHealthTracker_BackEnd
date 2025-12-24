package com.example.smarthealth.service;

import com.example.smarthealth.config.CurrentUserService;
import com.example.smarthealth.dto.health.StepResponse;
import com.example.smarthealth.dto.health.StepSyncRequest;
import com.example.smarthealth.model.auth.User;
import com.example.smarthealth.model.health.StepDaily;
import com.example.smarthealth.repository.StepDailyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class StepService {

    private final StepDailyRepository stepDailyRepository;
    private final CurrentUserService currentUserService;

    private static final double KCAL_PER_STEP = 0.04;
    private static final double KM_PER_STEP = 0.00076;

    @Transactional
    public StepResponse syncSteps(StepSyncRequest request) {
        User user = currentUserService.getCurrentUser();
        LocalDate targetDate = (request.getDate() != null) ? request.getDate() : LocalDate.now();

        StepDaily stepDaily = stepDailyRepository.findByUserIdAndDate(user.getId(), targetDate)
                .orElseGet(() -> StepDaily.builder()
                        .user(user)
                        .date(targetDate)
                        .totalSteps(0)
                        .build());

        stepDaily.setTotalSteps(request.getTotalSteps());
        
        // stepDaily.setDistanceKm(request.getTotalSteps() * KM_PER_STEP);
        // stepDaily.setKCalBurned(request.getTotalSteps() * KCAL_PER_STEP);

        StepDaily saved = stepDailyRepository.save(stepDaily);

        return convertToDto(saved);
    }

    public StepResponse getStepsByDate(LocalDate date) {
        User user = currentUserService.getCurrentUser();
        
        StepDaily stepDaily = stepDailyRepository.findByUserIdAndDate(user.getId(), date)
                .orElse(StepDaily.builder()
                        .user(user)
                        .date(date)
                        .totalSteps(0)
                        .build());

        return convertToDto(stepDaily);
    }
    
    public StepResponse getTodaySteps() {
        return getStepsByDate(LocalDate.now());
    }

    private StepResponse convertToDto(StepDaily entity) {
        int steps = entity.getTotalSteps() != null ? entity.getTotalSteps() : 0;
        return StepResponse.builder()
                .date(entity.getDate())
                .totalSteps(steps)
                .kCalBurned(steps * KCAL_PER_STEP)
                .distanceKm(steps * KM_PER_STEP)
                .build();
    }
}