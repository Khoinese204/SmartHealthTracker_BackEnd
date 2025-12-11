package com.example.smarthealth.dto.dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminDashboardDto {
    private long totalUsers;
    private long totalWorkouts;
    private long totalStepsToday;
    private long totalCaloriesToday;
}
