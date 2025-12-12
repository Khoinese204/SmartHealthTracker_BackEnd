package com.example.smarthealth.service;

import com.example.smarthealth.dto.dashboard.AdminDashboardDto;
import com.example.smarthealth.repository.UserRepository;
import com.example.smarthealth.repository.WorkoutRepository;
import com.example.smarthealth.repository.StepDailyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final WorkoutRepository workoutRepository;
    private final StepDailyRepository stepDailyRepository;

    public AdminDashboardDto getDashboard() {
        long totalUsers = userRepository.count(); // tất cả user
        long totalWorkouts = workoutRepository.count(); // số buổi workout
        long totalStepsToday = stepDailyRepository.sumStepsByDate(LocalDate.now());
        long totalCaloriesToday = workoutRepository
                .sumCaloriesByDate(LocalDate.now());

        return AdminDashboardDto.builder()
                .totalUsers(totalUsers)
                .totalWorkouts(totalWorkouts)
                .totalStepsToday(totalStepsToday)
                .totalCaloriesToday(totalCaloriesToday)
                .build();
    }
}
