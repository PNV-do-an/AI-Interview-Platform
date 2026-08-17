package com.aiinterview.platform.model.dto.response;

import com.aiinterview.platform.model.entity.AdminAuditLog;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AuditLogResponse {

    private Long id;
    private Long adminId;
    private String adminEmail;
    private Long targetUserId;
    private String action;
    private String detail;
    private LocalDateTime createdAt;

    public static AuditLogResponse from(AdminAuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .adminId(log.getAdminId())
                .adminEmail(log.getAdminEmail())
                .targetUserId(log.getTargetUserId())
                .action(log.getAction())
                .detail(log.getDetail())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
