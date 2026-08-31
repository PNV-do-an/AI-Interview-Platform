package com.aiinterview.platform.service;

import com.aiinterview.platform.model.dto.response.AuditLogResponse;
import com.aiinterview.platform.model.dto.response.UserDetailResponse;
import com.aiinterview.platform.model.dto.response.UserSummaryResponse;
import com.aiinterview.platform.model.entity.User;
import com.aiinterview.platform.model.enums.Role;
import org.springframework.data.domain.Page;

public interface AdminUserService {

    Page<UserSummaryResponse> getUsers(int page, int size, String search, String status, Role role);

    UserDetailResponse getUserDetail(Long userId);

    UserDetailResponse lockUser(User admin, Long targetUserId);

    UserDetailResponse unlockUser(User admin, Long targetUserId);

    void deleteUser(User admin, Long targetUserId);

    void resetPassword(User admin, Long targetUserId);

    UserDetailResponse changeRole(User admin, Long targetUserId, Role newRole);

    Page<AuditLogResponse> getAuditLogs(int page, int size);
}
