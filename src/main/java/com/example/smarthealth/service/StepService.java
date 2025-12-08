package com.example.smarthealth.service;

import com.example.smarthealth.dto.health.StepResponse;
import com.example.smarthealth.dto.health.StepSyncRequest;
import com.example.smarthealth.model.auth.User;
import com.example.smarthealth.model.health.StepDaily;
import com.example.smarthealth.repository.StepDailyRepository;
import com.example.smarthealth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class StepService {

    private final StepDailyRepository stepDailyRepository;
    private final UserRepository userRepository;

    private static final double KCAL_PER_STEP = 0.04;
    private static final double KM_PER_STEP = 0.00076;

    @Transactional
    public StepResponse syncSteps(Long userId, StepSyncRequest request) {
        LocalDate targetDate = (request.getDate() != null) ? request.getDate() : LocalDate.now();

        StepDaily stepDaily = stepDailyRepository.findByUserIdAndDate(userId, targetDate)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found"));

                    return StepDaily.builder()
                            .user(user)
                            .date(targetDate)
                            .totalSteps(0)
                            .build();
                });

        stepDaily.setTotalSteps(request.getTotalSteps());

        StepDaily saved = stepDailyRepository.save(stepDaily);

        return convertToDto(saved);
    }

    public StepResponse getStepsByDate(Long userId, LocalDate date) {
        StepDaily stepDaily = stepDailyRepository.findByUserIdAndDate(userId, date)
                .orElse(StepDaily.builder().date(date).totalSteps(0).build());

        return convertToDto(stepDaily);
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