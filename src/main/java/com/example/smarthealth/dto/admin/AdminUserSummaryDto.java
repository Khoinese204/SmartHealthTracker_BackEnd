package com.example.smarthealth.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminUserSummaryDto {
    private Long id;
    private String email;
    private String fullName;
    private String avatarUrl;
    private String role;
    private boolean active;
    private LocalDateTime createdAt;
}
