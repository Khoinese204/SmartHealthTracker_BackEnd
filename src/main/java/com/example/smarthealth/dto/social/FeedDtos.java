// src/main/java/com/example/smarthealth/dto/social/FeedDtos.java
package com.example.smarthealth.dto.social;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;

import com.example.smarthealth.enums.PostVisibility;

public class FeedDtos {

    @Builder
    @Getter
    public static class LikeUserResponse {
        private Long userId;
        private String fullName;
        private String avatarUrl;
        private Instant likedAt;
    }

    @Getter
    @Setter
    public static class SharePostRequest {
        private String message; // caption khi share (optional)
        private PostVisibility visibility; // PUBLIC | PRIVATE | GROUP (optional -> default PUBLIC)
        private Long groupId; // required if visibility = GROUP
    }

    @Getter
    @Setter
    public static class CreatePostRequest {
        @NotBlank
        private String content;
        private String imageUrl;
        private Long achievementUserId;
        private PostVisibility visibility;
        private Long groupId;
    }

    @Getter
    @Setter
    public static class UpdatePostRequest {
        private String content;          // optional
        private String imageUrl;         // optional
        private PostVisibility visibility; // optional
        private Long groupId;            // required if visibility=GROUP
    }

    @Getter
    @Setter
    public static class CreateCommentRequest {
        @NotBlank
        private String content;
    }

    @Builder
    @Getter
    public static class UserSummaryDto {
        private Long id;
        private String fullName;
        private String avatarUrl;
    }

    @Builder
    @Getter
    public static class FeedItemResponse {
        private Long id;
        private Long userId;
        private String content;
        private String imageUrl;
        private Long achievementUserId;
        private Instant createdAt;

        private long likeCount;
        private long commentCount;
        private boolean likedByMe;

        private boolean isShare; // giữ để FE tiện render
        private PostVisibility visibility; // visibility của POST gốc
    }

    @Builder
    @Getter
    public static class ShareResponse {
        private Long shareId;
        private PostVisibility visibility; // visibility của SHARE
        private Long groupId;
        private String message;
        private Instant createdAt;

        private UserSummaryDto sharedBy;
        private FeedItemResponse originalPost;
    }

    @Builder
    @Getter
    public static class CommentResponse {
        private Long id;
        private Long postId;
        private Long userId;
        private String content;
        private Instant createdAt;
    }
}
