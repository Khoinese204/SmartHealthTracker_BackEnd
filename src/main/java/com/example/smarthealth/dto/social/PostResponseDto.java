package com.example.smarthealth.dto.social;

import java.time.Instant;

import lombok.Data;

@Data
public class PostResponseDto {
    private Long id;
    private String content;
    private String imageUrl;
    private String visibility;

    private UserSummaryDto author;
    private Instant createdAt;

    private int likeCount;
    private int commentCount;
    private boolean likedByMe;

    // 🔥 SHARE INFO
    private boolean isShared;
    private SharedPostDto sharedPost; // null nếu không phải post share
}
