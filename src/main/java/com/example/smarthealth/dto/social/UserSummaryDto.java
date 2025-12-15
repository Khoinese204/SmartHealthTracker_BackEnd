package com.example.smarthealth.dto.social;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserSummaryDto {
    private Long id;
    private String fullName;
    private String avatarUrl;
}
