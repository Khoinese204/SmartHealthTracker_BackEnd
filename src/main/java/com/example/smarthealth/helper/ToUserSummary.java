package com.example.smarthealth.helper;

import com.example.smarthealth.dto.social.UserSummaryDto;
import com.example.smarthealth.model.auth.User;

public final class ToUserSummary {

    private ToUserSummary() {}

    public static UserSummaryDto from(User user) {
        return UserSummaryDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
}
