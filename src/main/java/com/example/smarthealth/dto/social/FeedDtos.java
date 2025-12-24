// src/main/java/com/example/smarthealth/dto/social/FeedDtos.java
package com.example.smarthealth.dto.social;

import com.example.smarthealth.enums.PostVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;

public class FeedDtos {

    // ========= Likes =========

    @Builder
    @Getter
    public static class LikeUserResponse {

        @Schema(description = "User who liked the post")
        private UserSummaryDto user;

        @Schema(description = "Time when user liked the post")
        private Instant likedAt;
    }

    // ========= Share =========

    @Getter
    @Setter
    public static class SharePostRequest {

        @Schema(nullable = true, example = "Nice post!", description = "Optional caption/message when sharing")
        private String message;

        @Schema(nullable = true, example = "PUBLIC", description = "Optional. Default is PUBLIC if omitted.")
        private PostVisibility visibility;

        @Schema(nullable = true, example = "null", description = "Only required when visibility=GROUP. Otherwise omit or send null.")
        private Long groupId;
    }

    // ========= Posts =========

    @Getter
    @Setter
    public static class CreatePostRequest {

        @NotBlank
        @Schema(example = "Hello world")
        private String content;

        @Schema(nullable = true, example = "https://.../image.jpg", description = "Optional image URL. Omit or send null if no image.")
        private String imageUrl;

        @Schema(nullable = true, example = "null", description = "Optional. If not set, omit or send null. Do NOT send 0.")
        private Long achievementUserId;

        @Schema(nullable = true, example = "PUBLIC", description = "Optional. Default is PUBLIC if omitted.")
        private PostVisibility visibility;

        @Schema(nullable = true, example = "null", description = "Only required when visibility=GROUP. Otherwise omit or send null. Do NOT send 0.")
        private Long groupId;
    }

    @Getter
    @Setter
    public static class UpdatePostRequest {

        @Schema(nullable = true, example = "Updated content")
        private String content;

        @Schema(nullable = true, example = "https://.../image.jpg", description = "Optional. Send null to remove image, or omit to keep unchanged.")
        private String imageUrl;

        @Schema(nullable = true, example = "PUBLIC", description = "Optional. If provided, visibility will be updated.")
        private PostVisibility visibility;

        @Schema(nullable = true, example = "null", description = "Only valid when visibility=GROUP. Otherwise must be null/omitted.")
        private Long groupId;
    }

    // ========= Comments =========

    @Getter
    @Setter
    public static class CreateCommentRequest {
        @NotBlank
        @Schema(example = "Nice!")
        private String content;
    }

    // ========= Responses =========

    @Builder
    @Getter
    public static class FeedItemResponse {
        private Long id;
        private UserSummaryDto user;
        private String content;
        private String imageUrl;

        @Schema(nullable = true, example = "null", description = "Optional. Null if post is not attached to any achievement.")
        private Long achievementUserId;

        private Instant createdAt;

        private long likeCount;
        private long commentCount;
        private boolean likedByMe;

        private boolean isShare; // FE convenience
        private PostVisibility visibility; // visibility of the original post
    }

    @Builder
    @Getter
    public static class ShareResponse {
        private Long shareId;

        @Schema(description = "Visibility of the share itself")
        private PostVisibility visibility;

        @Schema(nullable = true, example = "null", description = "Group ID the share was posted to (only when visibility=GROUP).")
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
        private UserSummaryDto user;
        private String content;
        private Instant createdAt;
    }
}
