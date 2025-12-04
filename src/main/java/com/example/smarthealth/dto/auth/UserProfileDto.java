package com.example.smarthealth.dto.auth;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class UserProfileDto {
    private Long id;
    private String email;
    private String fullName;
    private String avatarUrl;
    private LocalDate dateOfBirth;
    private String gender;
    private Integer heightCm;
    private BigDecimal weightKg;
    private String role;
}
