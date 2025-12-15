package com.example.smarthealth.dto.social;

import java.time.Instant;

import lombok.Data;

@Data
public class SharedPostDto {
    private Long id;
    private String content;
    private UserSummaryDto author;
    private Instant createdAt;
}