package com.example.smarthealth.service;

import com.example.smarthealth.config.CurrentUserService;
import com.example.smarthealth.dto.social.GroupDtos;
import com.example.smarthealth.enums.GroupMemberRole;
import com.example.smarthealth.model.auth.User;
import com.example.smarthealth.model.social.*;
import com.example.smarthealth.repository.GroupMemberRepository;
import com.example.smarthealth.repository.GroupRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public GroupDtos.GroupResponse createGroup(GroupDtos.CreateGroupRequest req) {
        User me = currentUserService.getCurrentUser();

        SocialGroup g = groupRepository.save(SocialGroup.builder()
                .ownerId(me.getId())
                .name(req.getName())
                .description(req.getDescription())
                .isPublic(req.isPublic())
                .build());

        // owner auto join (role NOT NULL)
        groupMemberRepository.save(GroupMember.builder()
                .groupId(g.getId())
                .userId(me.getId())
                .role(GroupMemberRole.OWNER)
                .build());

        return GroupDtos.GroupResponse.builder()
                .id(g.getId())
                .ownerId(g.getOwnerId())
                .name(g.getName())
                .description(g.getDescription())
                .isPublic(g.isPublic())
                .createdAt(g.getCreatedAt())
                .memberCount(1)
                .joinedByMe(true)
                .build();
    }

    @Transactional
    public void joinGroup(Long groupId) {
        User me = currentUserService.getCurrentUser();

        SocialGroup g = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found"));

        if (!g.isPublic()) {
            throw new IllegalStateException("This group is private (invite-only).");
        }

        if (groupMemberRepository.existsByGroupIdAndUserId(groupId, me.getId())) return;

        groupMemberRepository.save(GroupMember.builder()
                .groupId(groupId)
                .userId(me.getId())
                .role(GroupMemberRole.MEMBER)
                .build());
    }

    @Transactional(readOnly = true)
    public List<GroupDtos.GroupResponse> myGroups() {
        User me = currentUserService.getCurrentUser();
        List<Long> groupIds = groupMemberRepository.findGroupIdsByUserId(me.getId());
        if (groupIds.isEmpty()) return List.of();

        List<SocialGroup> groups = groupRepository.findAllById(groupIds);

        Map<Long, Long> memberCounts = groupMemberRepository.countMembersByGroupIds(groupIds)
                .stream().collect(Collectors.toMap(
                        r -> (Long) r[0],
                        r -> (Long) r[1]
                ));

        Set<Long> joined = new HashSet<>(groupIds);

        return groups.stream()
                .sorted(Comparator.comparing(SocialGroup::getCreatedAt).reversed())
                .map(g -> GroupDtos.GroupResponse.builder()
                        .id(g.getId())
                        .ownerId(g.getOwnerId())
                        .name(g.getName())
                        .description(g.getDescription())
                        .isPublic(g.isPublic())
                        .createdAt(g.getCreatedAt())
                        .memberCount(memberCounts.getOrDefault(g.getId(), 0L))
                        .joinedByMe(joined.contains(g.getId()))
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GroupDtos.GroupMemberResponse> members(Long groupId) {
        if (!groupRepository.existsById(groupId)) throw new EntityNotFoundException("Group not found");

        return groupMemberRepository.findByGroupIdOrderByJoinedAtAsc(groupId).stream()
                .map(m -> GroupDtos.GroupMemberResponse.builder()
                        .userId(m.getUserId())
                        .role(m.getRole().name())
                        .joinedAt(m.getJoinedAt())
                        .build())
                .toList();
    }
}
