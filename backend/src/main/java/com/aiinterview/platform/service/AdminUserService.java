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

    UserDetailResponse lockUser(Long targetUserId);

    UserDetailResponse unlockUser(Long targetUserId);

    void deleteUser(Long targetUserId);

    void resetPassword(Long targetUserId);

    UserDetailResponse changeRole(Long targetUserId, Role newRole);

    Page<AuditLogResponse> getAuditLogs(int page, int size);
}
