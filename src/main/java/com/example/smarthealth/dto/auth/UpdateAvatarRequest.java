package com.example.smarthealth.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAvatarRequest {

    @NotBlank(message = "avatarUrl is required")
    // optional: chỉ nhận https (an toàn hơn)
    @Pattern(regexp = "^https://.*", message = "avatarUrl must be https")
    private String avatarUrl;
}