package com.example.smarthealth.service;

import com.example.smarthealth.config.CurrentUserService;
import com.example.smarthealth.dto.social.GroupDtos;
import com.example.smarthealth.enums.GroupMemberRole;
import com.example.smarthealth.enums.InviteStatus;
import com.example.smarthealth.helper.ToUserSummary;
import com.example.smarthealth.model.auth.User;
import com.example.smarthealth.model.social.GroupInvite;
import com.example.smarthealth.model.social.GroupMember;

import com.example.smarthealth.model.social.SocialGroup;
import com.example.smarthealth.repository.GroupInviteRepository;
import com.example.smarthealth.repository.GroupMemberRepository;
import com.example.smarthealth.repository.GroupRepository;
import com.example.smarthealth.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

        private final CurrentUserService currentUserService;
        private final GroupRepository groupRepository;
        private final GroupMemberRepository groupMemberRepository;
        private final GroupInviteRepository groupInviteRepository;
        private final UserRepository userRepository;

        // =========================
        // CREATE GROUP (invite-only)
        // =========================
        @Transactional
        public GroupDtos.GroupResponse createGroup(GroupDtos.CreateGroupRequest req) {
                User me = currentUserService.getCurrentUser();

                SocialGroup g = groupRepository.save(SocialGroup.builder()
                                .ownerId(me.getId())
                                .name(req.getName())
                                .description(req.getDescription())
                                .isPublic(false) // ✅ hệ thống không có public group
                                .build());

                // owner auto join
                groupMemberRepository.save(GroupMember.builder()
                                .groupId(g.getId())
                                .userId(me.getId())
                                .role(GroupMemberRole.OWNER)
                                .build());

                return GroupDtos.GroupResponse.builder()
                                .id(g.getId())
                                .owner(ToUserSummary.from(me))
                                .name(g.getName())
                                .description(g.getDescription())
                                .isPublic(false)
                                .createdAt(g.getCreatedAt())
                                .memberCount(1)
                                .joinedByMe(true)
                                .build();
        }

        // =========================
        // JOIN GROUP (disabled)
        // =========================
        @Transactional
        public void joinGroup(Long groupId) {
                // ✅ invite-only => join phải đi qua acceptInvite(inviteId)
                throw new ResponseStatusException(
                                HttpStatus.FORBIDDEN,
                                "Invite-only. Please accept an invitation to join.");
        }

        // =========================
        // MY GROUPS
        // =========================
        @Transactional(readOnly = true)
        public List<GroupDtos.GroupResponse> myGroups() {
                User me = currentUserService.getCurrentUser();

                List<Long> groupIds = groupMemberRepository.findGroupIdsByUserId(me.getId());
                if (groupIds.isEmpty())
                        return List.of();

                List<SocialGroup> groups = groupRepository.findAllById(groupIds);

                Map<Long, Long> memberCounts = groupMemberRepository.countMembersByGroupIds(groupIds)
                                .stream()
                                .collect(Collectors.toMap(
                                                r -> (Long) r[0],
                                                r -> (Long) r[1]));

                Set<Long> ownerIds = groups.stream().map(SocialGroup::getOwnerId).collect(Collectors.toSet());
                Map<Long, User> ownersById = userRepository.findAllById(ownerIds).stream()
                                .collect(Collectors.toMap(User::getId, u -> u));

                Set<Long> joined = new HashSet<>(groupIds);

                return groups.stream()
                                .sorted(Comparator.comparing(SocialGroup::getCreatedAt).reversed())
                                .map(g -> GroupDtos.GroupResponse.builder()
                                                .id(g.getId())
                                                .owner(ToUserSummary.from(ownersById.get(g.getOwnerId())))
                                                .name(g.getName())
                                                .description(g.getDescription())
                                                .isPublic(false) // ✅ chốt
                                                .createdAt(g.getCreatedAt())
                                                .memberCount(memberCounts.getOrDefault(g.getId(), 0L))
                                                .joinedByMe(joined.contains(g.getId()))
                                                .build())
                                .toList();
        }

        // =========================
        // MEMBERS
        // =========================
        @Transactional(readOnly = true)
        public List<GroupDtos.GroupMemberResponse> members(Long groupId) {
                if (!groupRepository.existsById(groupId)) {
                        throw new EntityNotFoundException("Group not found");
                }

                List<GroupMember> members = groupMemberRepository.findByGroupIdOrderByJoinedAtAsc(groupId);

                Set<Long> userIds = members.stream().map(GroupMember::getUserId).collect(Collectors.toSet());
                Map<Long, User> usersById = userRepository.findAllById(userIds).stream()
                                .collect(Collectors.toMap(User::getId, u -> u));

                return members.stream()
                                .map(m -> GroupDtos.GroupMemberResponse.builder()
                                                .user(ToUserSummary.from(usersById.get(m.getUserId())))
                                                .role(m.getRole().name())
                                                .joinedAt(m.getJoinedAt())
                                                .build())
                                .toList();
        }

        // =========================
        // INVITE (create pending)
        // =========================
        @Transactional
        public GroupDtos.InviteResponse invite(Long groupId, GroupDtos.CreateInviteRequest req) {
                User me = currentUserService.getCurrentUser();

                SocialGroup g = groupRepository.findById(groupId)
                                .orElseThrow(() -> new EntityNotFoundException("Group not found"));

                // chỉ OWNER được invite
                GroupMember myMembership = groupMemberRepository.findByGroupIdAndUserId(groupId, me.getId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                                                "You are not a member of this group"));

                if (myMembership.getRole() != GroupMemberRole.OWNER) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only group owner can invite members");
                }

                if (req.getInvitedUserId() == null) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invitedUserId is required");
                }
                if (req.getInvitedUserId().equals(me.getId())) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot invite yourself");
                }

                User invited = userRepository.findById(req.getInvitedUserId())
                                .orElseThrow(() -> new EntityNotFoundException("Invited user not found"));

                // đã là member
                if (groupMemberRepository.existsByGroupIdAndUserId(groupId, invited.getId())) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "User already joined this group");
                }

                // đã có pending
                if (groupInviteRepository.existsByGroupIdAndInvitedUserIdAndStatus(groupId, invited.getId(),
                                InviteStatus.PENDING)) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Invitation is already pending");
                }

                Instant now = Instant.now();
                Instant expiresAt = now.plus(7, ChronoUnit.DAYS);

                GroupInvite inv = groupInviteRepository.save(GroupInvite.builder()
                                .groupId(g.getId())
                                .invitedUserId(invited.getId())
                                .invitedByUserId(me.getId())
                                .relation(req.getRelation())
                                .status(InviteStatus.PENDING)
                                .createdAt(now)
                                .expiresAt(expiresAt)
                                .build());

                return GroupDtos.InviteResponse.builder()
                                .inviteId(inv.getId())
                                .groupId(g.getId())
                                .invitedUser(ToUserSummary.from(invited))
                                .relation(inv.getRelation())
                                .status(inv.getStatus().name())
                                .createdAt(inv.getCreatedAt())
                                .expiresAt(inv.getExpiresAt())
                                .build();
        }

        // =========================
        // PENDING INVITES (my)
        // =========================
        @Transactional(readOnly = true)
        public List<GroupDtos.PendingInviteResponse> myPendingInvites() {
                User me = currentUserService.getCurrentUser();

                List<GroupInvite> invites = groupInviteRepository
                                .findByInvitedUserIdAndStatusOrderByCreatedAtDesc(me.getId(), InviteStatus.PENDING);

                if (invites.isEmpty())
                        return List.of();

                // preload groups + inviters
                Set<Long> groupIds = invites.stream().map(GroupInvite::getGroupId).collect(Collectors.toSet());
                Map<Long, SocialGroup> groupsById = groupRepository.findAllById(groupIds).stream()
                                .collect(Collectors.toMap(SocialGroup::getId, x -> x));

                Set<Long> inviterIds = invites.stream().map(GroupInvite::getInvitedByUserId)
                                .collect(Collectors.toSet());
                Map<Long, User> invitersById = userRepository.findAllById(inviterIds).stream()
                                .collect(Collectors.toMap(User::getId, x -> x));

                Instant now = Instant.now();

                return invites.stream()
                                .filter(inv -> inv.getExpiresAt() == null || inv.getExpiresAt().isAfter(now))
                                .map(inv -> {
                                        SocialGroup g = groupsById.get(inv.getGroupId());
                                        User inviter = invitersById.get(inv.getInvitedByUserId());

                                        return GroupDtos.PendingInviteResponse.builder()
                                                        .inviteId(inv.getId())
                                                        .groupId(inv.getGroupId())
                                                        .groupName(g != null ? g.getName() : "(deleted)")
                                                        .groupDescription(g != null ? g.getDescription() : null)
                                                        .invitedBy(inviter != null ? ToUserSummary.from(inviter) : null)
                                                        .relation(inv.getRelation())
                                                        .createdAt(inv.getCreatedAt())
                                                        .expiresAt(inv.getExpiresAt())
                                                        .build();
                                })
                                .toList();
        }

        // =========================
        // ACCEPT INVITE -> join group
        // =========================
        @Transactional
        public void acceptInvite(Long inviteId) {
                User me = currentUserService.getCurrentUser();

                GroupInvite inv = groupInviteRepository.findByIdAndInvitedUserId(inviteId, me.getId())
                                .orElseThrow(() -> new EntityNotFoundException("Invitation not found"));

                if (inv.getStatus() != InviteStatus.PENDING) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Invitation is not pending");
                }

                if (inv.getExpiresAt() != null && inv.getExpiresAt().isBefore(Instant.now())) {
                        inv.setStatus(InviteStatus.EXPIRED);
                        groupInviteRepository.save(inv);
                        throw new ResponseStatusException(HttpStatus.GONE, "Invitation has expired");
                }

                // group còn tồn tại không
                if (!groupRepository.existsById(inv.getGroupId())) {
                        throw new ResponseStatusException(HttpStatus.GONE, "Group no longer exists");
                }

                // insert member (idempotent)
                if (!groupMemberRepository.existsByGroupIdAndUserId(inv.getGroupId(), me.getId())) {
                        groupMemberRepository.save(GroupMember.builder()
                                        .groupId(inv.getGroupId())
                                        .userId(me.getId())
                                        .role(GroupMemberRole.MEMBER)
                                        .build());
                }

                inv.setStatus(InviteStatus.ACCEPTED);
                groupInviteRepository.save(inv);
        }

        // =========================
        // DECLINE INVITE
        // =========================
        @Transactional
        public void declineInvite(Long inviteId) {
                User me = currentUserService.getCurrentUser();

                GroupInvite inv = groupInviteRepository.findByIdAndInvitedUserId(inviteId, me.getId())
                                .orElseThrow(() -> new EntityNotFoundException("Invitation not found"));

                if (inv.getStatus() != InviteStatus.PENDING) {
                        return; // idempotent
                }

                inv.setStatus(InviteStatus.DECLINED);
                groupInviteRepository.save(inv);
        }

        // =========================
        // OPTIONAL: LEAVE GROUP
        // =========================
        @Transactional
        public void leaveGroup(Long groupId) {
                User me = currentUserService.getCurrentUser();

                GroupMember membership = groupMemberRepository.findByGroupIdAndUserId(groupId, me.getId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Membership not found"));

                if (membership.getRole() == GroupMemberRole.OWNER) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Owner cannot leave the group");
                }

                groupMemberRepository.delete(membership);
        }

        @Transactional
        public void removeMember(Long groupId, Long memberUserId) {
                User me = currentUserService.getCurrentUser();

                // group exists?
                if (!groupRepository.existsById(groupId)) {
                        throw new EntityNotFoundException("Group not found");
                }

                // permission: must be OWNER
                GroupMember myMembership = groupMemberRepository.findByGroupIdAndUserId(groupId, me.getId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                                                "You are not a member of this group"));

                if (myMembership.getRole() != GroupMemberRole.OWNER) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only group owner can remove members");
                }

                // cannot remove yourself here (use leaveGroup or ownership transfer feature)
                if (Objects.equals(me.getId(), memberUserId)) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Owner cannot remove themselves");
                }

                // target membership
                GroupMember target = groupMemberRepository.findByGroupIdAndUserId(groupId, memberUserId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Member not found"));

                // cannot remove owner
                if (target.getRole() == GroupMemberRole.OWNER) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot remove group owner");
                }

                groupMemberRepository.delete(target);
        }

        @Transactional
        public void revokeInvite(Long groupId, Long inviteId) {
                User me = currentUserService.getCurrentUser();

                // group exists?
                if (!groupRepository.existsById(groupId)) {
                        throw new EntityNotFoundException("Group not found");
                }

                // permission: must be OWNER
                GroupMember myMembership = groupMemberRepository.findByGroupIdAndUserId(groupId, me.getId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                                                "You are not a member of this group"));

                if (myMembership.getRole() != GroupMemberRole.OWNER) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                                        "Only group owner can revoke invitations");
                }

                GroupInvite inv = groupInviteRepository.findById(inviteId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Invitation not found"));

                // ensure invite belongs to this group
                if (!Objects.equals(inv.getGroupId(), groupId)) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        "Invitation does not belong to this group");
                }

                // Only pending can be revoked
                if (inv.getStatus() != InviteStatus.PENDING) {
                        return; // idempotent: already handled
                }

                // Option A: hard delete (recommended for "revoke")
                groupInviteRepository.delete(inv);

                // Option B: soft status (nếu bạn muốn audit) -> dùng dòng dưới thay delete
                // inv.setStatus(InviteStatus.DECLINED);
                // groupInviteRepository.save(inv);
        }

}
