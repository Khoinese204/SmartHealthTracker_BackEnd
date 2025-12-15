package com.example.smarthealth.dto.social;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;

public class GroupDtos {

    @Getter
    @Setter
    public static class CreateGroupRequest {
        @NotBlank
        private String name;
        private String description;
        private boolean isPublic = true;
    }

    @Builder
    @Getter
    public static class GroupResponse {
        private Long id;
        private Long ownerId;
        private String name;
        private String description;
        private boolean isPublic;
        private Instant createdAt;

        private long memberCount;
        private boolean joinedByMe;
    }

    @Builder
    @Getter
    public static class GroupMemberResponse {
        private Long userId;
        private String role; // OWNER/MEMBER
        private Instant joinedAt;
    }
}
