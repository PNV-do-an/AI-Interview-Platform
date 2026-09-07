package com.aiinterview.platform.model.dto.response;

import com.aiinterview.platform.model.entity.User;
import com.aiinterview.platform.model.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserSummaryResponse {

    private Long id;
    private String email;
    private String fullName;
    private Role role;
    private String status;   // ACTIVE | INACTIVE | LOCKED | DELETED
    private LocalDateTime createdAt;

    public static UserSummaryResponse from(User user) {
        return UserSummaryResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .status(resolveStatus(user))
                .createdAt(user.getCreatedAt())
                .build();
    }

    private static String resolveStatus(User user) {
        if (user.getDeletedAt() != null)  return "DELETED";
        if (user.isLocked())              return "LOCKED";
        if (!user.isEnabled())            return "INACTIVE";
        return "ACTIVE";
    }
}
