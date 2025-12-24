package com.example.smarthealth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.smarthealth.enums.InviteStatus;
import com.example.smarthealth.model.social.GroupInvite;

public interface GroupInviteRepository extends JpaRepository<GroupInvite, Long> {

    boolean existsByGroupIdAndInvitedUserIdAndStatus(
            Long groupId,
            Long invitedUserId,
            InviteStatus status);

    List<GroupInvite> findByInvitedUserIdAndStatusOrderByCreatedAtDesc(
            Long invitedUserId,
            InviteStatus status);

    Optional<GroupInvite> findByIdAndInvitedUserId(
            Long id,
            Long invitedUserId);
}
