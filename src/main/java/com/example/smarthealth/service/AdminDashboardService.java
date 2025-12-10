package com.example.smarthealth.service;

import com.example.smarthealth.dto.dashboard.AdminDashboardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    // Inject repository nếu cần
    // private final UserRepository userRepository;
    // private final WorkoutRepository workoutRepository;

    public AdminDashboardResponse getDashboard() {

        // TODO: Query thật từ DB
        long totalUsers = 100;
        long totalWorkouts = 350;
        long totalStepsToday = 75000;
        long totalCaloriesToday = 12000;

        return AdminDashboardResponse.builder()
                .totalUsers(totalUsers)
                .totalWorkouts(totalWorkouts)
                .totalStepsToday(totalStepsToday)
                .totalCaloriesToday(totalCaloriesToday)
                .build();
    }
}
