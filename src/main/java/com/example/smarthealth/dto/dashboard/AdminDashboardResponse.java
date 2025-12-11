package com.example.smarthealth.dto.dashboard;

import com.example.smarthealth.dto.common.ApiSuccessResponseExample;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Admin dashboard API Response")
public class AdminDashboardResponse extends ApiSuccessResponseExample<AdminDashboardDto> {
    public AdminDashboardResponse(int status, String message, AdminDashboardDto data) {
        super(status, message, data);
    }
}