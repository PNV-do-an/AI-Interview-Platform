package com.aiinterview.platform.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin_audit_log")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long adminId;

    @Column(nullable = false)
    private String adminEmail;

    /** The user that was affected by the action (nullable for non-user actions) */
    @Column
    private Long targetUserId;

    /** Action identifier, e.g. LOCK_USER, UNLOCK_USER, DELETE_USER, RESET_PASSWORD, CHANGE_ROLE */
    @Column(nullable = false, length = 100)
    private String action;

    /** Human-readable detail describing what changed */
    @Column(columnDefinition = "TEXT")
    private String detail;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
