package com.example.smarthealth.model.social;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

import com.example.smarthealth.enums.InviteStatus;

@Entity
@Table(name = "group_invites", indexes = {
        @Index(name = "idx_group_invites_invited_user_status", columnList = "invited_user_id,status"),
        @Index(name = "idx_group_invites_group", columnList = "group_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "invited_user_id", nullable = false)
    private Long invitedUserId;

    @Column(name = "invited_by_user_id", nullable = false)
    private Long invitedByUserId;

    // relation bạn đang để String ở DTO, mình vẫn map String cho dễ (sau muốn đổi
    // enum rất dễ)
    @Column(nullable = false, length = 30)
    private String relation; // FATHER/MOTHER/BROTHER/SISTER/OTHER

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InviteStatus status; // PENDING/ACCEPTED/DECLINED/EXPIRED

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null)
            createdAt = Instant.now();
    }
}
