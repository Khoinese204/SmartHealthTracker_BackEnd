package com.example.smarthealth.dto.auth;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UserProfileUpdateRequest {
    private String fullName;
    private String gender;
    private Integer heightCm;
    private Double weightKg;
    private LocalDate dateOfBirth;
}
