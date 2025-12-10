package com.example.smarthealth.dto.auth;

import com.example.smarthealth.dto.common.ApiResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User Profile API Response")
public class UserProfileResponse extends ApiResponse<UserProfileDto> {
    public UserProfileResponse(int status, String message, UserProfileDto data) {
        super(status, message, data);
    }
}