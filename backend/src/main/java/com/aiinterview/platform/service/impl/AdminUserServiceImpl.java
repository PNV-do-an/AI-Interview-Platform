package com.aiinterview.platform.service.impl;

import com.aiinterview.platform.common.exception.BadRequestException;
import com.aiinterview.platform.common.exception.ResourceNotFoundException;
import com.aiinterview.platform.model.dto.response.AuditLogResponse;
import com.aiinterview.platform.model.dto.response.UserDetailResponse;
import com.aiinterview.platform.model.dto.response.UserSummaryResponse;
import com.aiinterview.platform.model.entity.AdminAuditLog;
import com.aiinterview.platform.model.entity.User;
import com.aiinterview.platform.model.enums.Role;
import com.aiinterview.platform.model.repository.AdminAuditLogRepository;
import com.aiinterview.platform.model.repository.UserRepository;
import com.aiinterview.platform.service.AdminUserService;
import com.aiinterview.platform.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final AdminAuditLogRepository auditLogRepository;
    private final EmailService emailService;

    @Override
    public Page<UserSummaryResponse> getUsers(int page, int size, String search, String status, Role role) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        String searchParam = (search != null && !search.isBlank()) ? search.trim() : null;
        String statusParam = (status != null && !status.isBlank()) ? status.trim().toUpperCase() : null;
        return userRepository.findAllWithFilters(searchParam, role, statusParam, pageable)
                .map(UserSummaryResponse::from);
    }

    @Override
    public UserDetailResponse getUserDetail(Long userId) {
        User user = findUserOrThrow(userId);
        User admin = resolveAdmin();
        saveAuditLog(admin, userId, "VIEW_USER", "Xem chi tiết tài khoản: " + user.getEmail());
        return UserDetailResponse.from(user);
    }

    @Override
    @Transactional
    public UserDetailResponse lockUser(Long targetUserId) {
        User admin = resolveAdmin();
        User target = findUserOrThrow(targetUserId);
        if (target.getDeletedAt() != null) {
            throw new BadRequestException("Không thể khóa tài khoản đã bị xóa");
        }
        if (target.isLocked()) {
            throw new BadRequestException("Tài khoản đã bị khóa");
        }
        if (admin != null && admin.getId().equals(targetUserId)) {
            throw new BadRequestException("Không thể khóa tài khoản của chính mình");
        }
        target.setLocked(true);
        userRepository.save(target);
        saveAuditLog(admin, targetUserId, "LOCK_USER", "Khóa tài khoản: " + target.getEmail());
        try {
            emailService.sendAccountLockedEmail(target.getEmail(), target.getFullName());
        } catch (Exception e) {
            log.warn("Không thể gửi email khóa tài khoản tới {}: {}", target.getEmail(), e.getMessage());
        }
        return UserDetailResponse.from(target);
    }

    @Override
    @Transactional
    public UserDetailResponse unlockUser(Long targetUserId) {
        User admin = resolveAdmin();
        User target = findUserOrThrow(targetUserId);
        if (!target.isLocked()) {
            throw new BadRequestException("Tài khoản không trong trạng thái bị khóa");
        }
        target.setLocked(false);
        userRepository.save(target);
        saveAuditLog(admin, targetUserId, "UNLOCK_USER", "Mở khóa tài khoản: " + target.getEmail());
        try {
            emailService.sendAccountUnlockedEmail(target.getEmail(), target.getFullName());
        } catch (Exception e) {
            log.warn("Không thể gửi email mở khóa tài khoản tới {}: {}", target.getEmail(), e.getMessage());
        }
        return UserDetailResponse.from(target);
    }

    @Override
    @Transactional
    public void deleteUser(Long targetUserId) {
        User admin = resolveAdmin();
        if (admin != null && admin.getId().equals(targetUserId)) {
            throw new BadRequestException("Không thể xóa tài khoản của chính mình");
        }
        User target = findUserOrThrow(targetUserId);
        if (target.getDeletedAt() != null) {
            throw new BadRequestException("Tài khoản đã bị xóa");
        }
        target.setDeletedAt(LocalDateTime.now());
        userRepository.save(target);
        saveAuditLog(admin, targetUserId, "DELETE_USER", "Xóa tài khoản: " + target.getEmail());
        try {
            emailService.sendAccountDeletedEmail(target.getEmail(), target.getFullName());
        } catch (Exception e) {
            log.warn("Không thể gửi email xóa tài khoản tới {}: {}", target.getEmail(), e.getMessage());
        }
    }

    @Override
    @Transactional
    public void resetPassword(Long targetUserId) {
        User admin = resolveAdmin();
        User target = findUserOrThrow(targetUserId);
        if (target.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Không tìm thấy tài khoản");
        }
        String resetToken = UUID.randomUUID().toString();
        target.setResetToken(resetToken);
        target.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(target);
        saveAuditLog(admin, targetUserId, "RESET_PASSWORD", "Gửi email reset mật khẩu: " + target.getEmail());
        try {
            emailService.sendResetPasswordEmail(target.getEmail(), target.getFullName(), resetToken);
        } catch (Exception e) {
            log.warn("Không thể gửi email reset password tới {}: {}", target.getEmail(), e.getMessage());
        }
    }

    @Override
    @Transactional
    public UserDetailResponse changeRole(Long targetUserId, Role newRole) {
        User admin = resolveAdmin();
        if (admin != null && admin.getId().equals(targetUserId)) {
            throw new BadRequestException("Không thể thay đổi vai trò của chính mình");
        }
        User target = findUserOrThrow(targetUserId);
        if (target.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Không tìm thấy tài khoản");
        }
        if (target.getRole() == Role.ROLE_ADMIN && newRole != Role.ROLE_ADMIN) {
            Page<User> adminPage = userRepository.findAllWithFilters(null, Role.ROLE_ADMIN, null, Pageable.unpaged());
            long adminCount = adminPage.getContent().size();
            if (adminCount <= 1) {
                throw new BadRequestException("Không thể thay đổi vai trò: phải luôn có ít nhất một tài khoản Quản trị viên trong hệ thống");
            }
        }
        String detail = String.format("Đổi role: %s → %s | user: %s",
                target.getRole().name(), newRole.name(), target.getEmail());
        target.setRole(newRole);
        userRepository.save(target);
        saveAuditLog(admin, targetUserId, "CHANGE_ROLE", detail);
        return UserDetailResponse.from(target);
    }

    @Override
    public Page<AuditLogResponse> getAuditLogs(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return auditLogRepository.findAll(pageable).map(AuditLogResponse::from);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với id: " + userId));
    }

    /**
     * Resolve the currently authenticated admin User entity from SecurityContext.
     * Returns null if the context has no valid authentication, or the principal
     * cannot be mapped to a User (e.g. in tests with @WithMockUser).
     */
    private User resolveAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof User u) {
            return u;
        }
        String email = null;
        if (principal instanceof UserDetails ud) {
            email = ud.getUsername();
        } else if (principal instanceof String s) {
            email = s;
        }
        if (email != null) {
            Optional<User> opt = userRepository.findByEmail(email);
            if (opt.isPresent()) return opt.get();
        }
        return null;
    }

    private void saveAuditLog(User admin, Long targetUserId, String action, String detail) {
        Long adminId = admin != null ? admin.getId() : 0L;
        String adminEmail = admin != null ? admin.getEmail() : "system";
        AdminAuditLog log = AdminAuditLog.builder()
                .adminId(adminId)
                .adminEmail(adminEmail)
                .targetUserId(targetUserId)
                .action(action)
                .detail(detail)
                .build();
        auditLogRepository.save(log);
    }
}
