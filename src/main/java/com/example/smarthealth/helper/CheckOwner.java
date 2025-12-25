package com.example.smarthealth.helper;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.example.smarthealth.enums.PostVisibility;
import com.example.smarthealth.enums.GroupMemberRole;
import com.example.smarthealth.model.auth.User;
import com.example.smarthealth.model.social.Post;
import com.example.smarthealth.repository.GroupMemberRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CheckOwner {

    private final GroupMemberRepository groupMemberRepository;

    /**
     * Group OWNER có quyền moderate (sửa/xoá) post trong group
     */
    public boolean canModerateGroupPost(User me, Post post) {
        if (post.getGroupId() == null)
            return false;

        return groupMemberRepository.existsByGroupIdAndUserIdAndRole(
                post.getGroupId(),
                me.getId(),
                GroupMemberRole.OWNER);
    }

    /**
     * Rule tổng:
     * - Author luôn được sửa/xoá
     * - Nếu là post GROUP thì group OWNER được moderate
     */
    public boolean canEditOrDelete(User me, Post post) {
        // author luôn được phép
        if (Objects.equals(post.getUserId(), me.getId())) {
            return true;
        }

        // post trong group => group owner được phép
        if (post.getVisibility() == PostVisibility.GROUP && post.getGroupId() != null) {
            return canModerateGroupPost(me, post);
        }

        return false;
    }

    public boolean isGroupOwner(Long groupId, Long userId) {
        return groupMemberRepository.existsByGroupIdAndUserIdAndRole(
                groupId,
                userId,
                GroupMemberRole.OWNER);
    }
}
