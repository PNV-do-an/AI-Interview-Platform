package com.aiinterview.platform.controller;

import com.aiinterview.platform.common.response.ApiResponse;
import com.aiinterview.platform.model.dto.request.ChangeRoleRequest;
import com.aiinterview.platform.model.dto.response.AuditLogResponse;
import com.aiinterview.platform.model.dto.response.UserDetailResponse;
import com.aiinterview.platform.model.dto.response.UserSummaryResponse;
import com.aiinterview.platform.model.enums.Role;
import com.aiinterview.platform.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    /** GET /api/v1/admin/users?page=0&size=20&search=...&status=ACTIVE&role=ROLE_USER */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserSummaryResponse>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Role role) {
        Page<UserSummaryResponse> users = adminUserService.getUsers(page, size, search, status, role);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    /** GET /api/v1/admin/users/{id} */
    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserDetailResponse>> getUserDetail(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(adminUserService.getUserDetail(id)));
    }

    /** PATCH /api/v1/admin/users/{id}/lock */
    @PatchMapping("/users/{id}/lock")
    public ResponseEntity<ApiResponse<UserDetailResponse>> lockUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Tài khoản đã bị khóa", adminUserService.lockUser(id)));
    }

    /** PATCH /api/v1/admin/users/{id}/unlock */
    @PatchMapping("/users/{id}/unlock")
    public ResponseEntity<ApiResponse<UserDetailResponse>> unlockUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Tài khoản đã được mở khóa", adminUserService.unlockUser(id)));
    }

    /** DELETE /api/v1/admin/users/{id} */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("Tài khoản đã được xóa", null));
    }

    /** POST /api/v1/admin/users/{id}/reset-password */
    @PostMapping("/users/{id}/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@PathVariable Long id) {
        adminUserService.resetPassword(id);
        return ResponseEntity.ok(ApiResponse.success("Email đặt lại mật khẩu đã được gửi", null));
    }

    /** PATCH /api/v1/admin/users/{id}/role */
    @PatchMapping("/users/{id}/role")
    public ResponseEntity<ApiResponse<UserDetailResponse>> changeRole(
            @PathVariable Long id,
            @Valid @RequestBody ChangeRoleRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Vai trò đã được cập nhật",
                adminUserService.changeRole(id, request.getRole())));
    }

    /** GET /api/v1/admin/audit-logs?page=0&size=20 */
    @GetMapping("/audit-logs")
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(adminUserService.getAuditLogs(page, size)));
    }
}
