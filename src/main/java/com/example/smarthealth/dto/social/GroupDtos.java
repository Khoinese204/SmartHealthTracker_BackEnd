package com.example.smarthealth.dto.social;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;

@Schema(description = "Group related DTOs")
public class GroupDtos {

    // =====================
    // CREATE GROUP
    // =====================
    @Getter
    @Setter
    @Schema(description = "Request body to create a new private group")
    public static class CreateGroupRequest {

        @NotBlank
        @Schema(description = "Group name", example = "Gia đình Trương")
        private String name;

        @Schema(description = "Group description", example = "Nhóm theo dõi sức khoẻ của gia đình")
        private String description;
    }

    // =====================
    // GROUP (MY GROUPS)
    // =====================
    @Builder
    @Getter
    @Schema(description = "Group information returned in My Groups")
    public static class GroupResponse {

        @Schema(example = "1")
        private Long id;

        @Schema(description = "Group owner")
        private UserSummaryDto owner;

        @Schema(example = "Gia đình Trương")
        private String name;

        @Schema(example = "Nhóm theo dõi sức khoẻ của gia đình")
        private String description;

        @Schema(description = "Public flag (always false – invite-only)", example = "false")
        private boolean isPublic = false;

        @Schema(example = "2025-01-01T10:15:30Z")
        private Instant createdAt;

        @Schema(description = "Total members in group", example = "5")
        private long memberCount;

        @Schema(description = "Whether current user already joined this group", example = "true")
        private boolean joinedByMe;
    }

    // =====================
    // GROUP MEMBER
    // =====================
    @Builder
    @Getter
    @Schema(description = "Group member information")
    public static class GroupMemberResponse {

        @Schema(description = "User summary of the member")
        private UserSummaryDto user;

        @Schema(description = "Role of member in group", example = "OWNER")
        private String role;

        @Schema(example = "2025-01-01T10:20:00Z")
        private Instant joinedAt;
    }

    // =====================
    // INVITATION
    // =====================
    @Getter
    @Setter
    @Schema(description = "Request body to invite a user to a group")
    public static class CreateInviteRequest {

        @Schema(description = "ID of the user being invited", example = "12")
        private Long invitedUserId;

        @NotBlank
        @Schema(description = "Relationship between inviter and invited user", example = "FATHER", allowableValues = {
                "FATHER",
                "MOTHER",
                "BROTHER",
                "SISTER",
                "OTHER"
        })
        private String relation;
    }

    @Builder
    @Getter
    @Schema(description = "Response after creating an invitation")
    public static class InviteResponse {

        @Schema(example = "100")
        private Long inviteId;

        @Schema(example = "1")
        private Long groupId;

        @Schema(description = "Invited user summary")
        private UserSummaryDto invitedUser;

        @Schema(example = "BROTHER")
        private String relation;

        @Schema(description = "Invitation status", example = "PENDING")
        private String status;

        @Schema(example = "2025-01-01T10:30:00Z")
        private Instant createdAt;

        @Schema(example = "2025-01-08T10:30:00Z")
        private Instant expiresAt;
    }

    // =====================
    // PENDING INVITATIONS
    // =====================
    @Builder
    @Getter
    @Schema(description = "Pending invitation for current user")
    public static class PendingInviteResponse {

        @Schema(example = "100")
        private Long inviteId;

        @Schema(example = "1")
        private Long groupId;

        @Schema(example = "Gia đình Trương")
        private String groupName;

        @Schema(example = "Nhóm theo dõi sức khoẻ của gia đình")
        private String groupDescription;

        @Schema(description = "User who sent the invitation")
        private UserSummaryDto invitedBy;

        @Schema(example = "MOTHER")
        private String relation;

        @Schema(example = "2025-01-01T10:30:00Z")
        private Instant createdAt;

        @Schema(example = "2025-01-08T10:30:00Z")
        private Instant expiresAt;
    }

    @Builder
    @Getter
    public static class InviteSearchUserResponse {
        private UserSummaryDto user; // id/fullName/avatarUrl
        private String email; // để FE hiển thị
        private boolean alreadyMember; // disable invite
        private boolean pendingInvited; // disable invite
    }
}
