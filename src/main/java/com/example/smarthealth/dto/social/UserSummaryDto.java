package com.example.smarthealth.dto.social;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserSummaryDto {
    private Long id;
    private String fullName;
    private String avatarUrl;
}
