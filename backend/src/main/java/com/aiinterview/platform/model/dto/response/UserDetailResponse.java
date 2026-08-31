package com.aiinterview.platform.model.dto.response;

import com.aiinterview.platform.model.entity.User;
import com.aiinterview.platform.model.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserDetailResponse {

    private Long id;
    private String email;
    private String fullName;
    private Role role;
    private boolean enabled;
    private boolean locked;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static UserDetailResponse from(User user) {
        return UserDetailResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .locked(user.isLocked())
                .status(resolveStatus(user))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .deletedAt(user.getDeletedAt())
                .build();
    }

    private static String resolveStatus(User user) {
        if (user.getDeletedAt() != null)  return "DELETED";
        if (user.isLocked())              return "LOCKED";
        if (!user.isEnabled())            return "INACTIVE";
        return "ACTIVE";
    }
}
