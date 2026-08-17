package com.aiinterview.platform.controller;

import com.aiinterview.platform.common.response.ApiResponse;
import com.aiinterview.platform.model.dto.request.ChangeRoleRequest;
import com.aiinterview.platform.model.dto.response.UserDetailResponse;
import com.aiinterview.platform.model.dto.response.UserSummaryResponse;
import com.aiinterview.platform.model.entity.User;
import com.aiinterview.platform.model.enums.Role;
import com.aiinterview.platform.model.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Admin User Controller Integration Tests")
class AdminUserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User adminUser;
    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        // Create admin user
        adminUser = User.builder()
                .email("admin@example.com")
                .password(passwordEncoder.encode("password123"))
                .fullName("Admin User")
                .role(Role.ROLE_ADMIN)
                .locked(false)
                .build();
        adminUser = userRepository.save(adminUser);

        // Create test user
        testUser = User.builder()
                .email("user@example.com")
                .password(passwordEncoder.encode("password123"))
                .fullName("Test User")
                .role(Role.ROLE_USER)
                .locked(false)
                .build();
        testUser = userRepository.save(testUser);
    }

    // ==================== Get Users Tests ====================

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get all users with pagination")
    void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users")
                .param("page", "0")
                .param("size", "20")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(greaterThanOrEqualTo(2)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get users with search filter")
    void testGetUsersWithSearch() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users")
                .param("page", "0")
                .param("size", "20")
                .param("search", "admin")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get users with status filter")
    void testGetUsersWithStatusFilter() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users")
                .param("page", "0")
                .param("size", "20")
                .param("status", "ACTIVE")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get users with role filter")
    void testGetUsersWithRoleFilter() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users")
                .param("page", "0")
                .param("size", "20")
                .param("role", "ROLE_USER")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ==================== Get User Detail Tests ====================

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get user detail by ID")
    void testGetUserDetail() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users/{id}", testUser.getId())
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value(testUser.getEmail()))
                .andExpect(jsonPath("$.data.fullName").value(testUser.getFullName()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 404 when user not found")
    void testGetUserDetail_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users/99999")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ==================== Lock/Unlock User Tests ====================

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should lock user successfully")
    void testLockUser() throws Exception {
        mockMvc.perform(patch("/api/v1/admin/users/{id}/lock", testUser.getId())
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Tài khoản đã bị khóa"));

        User lockedUser = userRepository.findById(testUser.getId()).get();
        assert lockedUser.isLocked();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should not lock already locked user")
    void testLockUser_AlreadyLocked() throws Exception {
        testUser.setLocked(true);
        userRepository.save(testUser);

        mockMvc.perform(patch("/api/v1/admin/users/{id}/lock", testUser.getId())
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should unlock user successfully")
    void testUnlockUser() throws Exception {
        testUser.setLocked(true);
        userRepository.save(testUser);

        mockMvc.perform(patch("/api/v1/admin/users/{id}/unlock", testUser.getId())
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Tài khoản đã được mở khóa"));

        User unlockedUser = userRepository.findById(testUser.getId()).get();
        assert !unlockedUser.isLocked();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should not unlock user that is not locked")
    void testUnlockUser_NotLocked() throws Exception {
        mockMvc.perform(patch("/api/v1/admin/users/{id}/unlock", testUser.getId())
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ==================== Delete User Tests ====================

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should delete user successfully")
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/v1/admin/users/{id}", testUser.getId())
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Tài khoản đã được xóa"));

        User deletedUser = userRepository.findById(testUser.getId()).get();
        assert deletedUser.getDeletedAt() != null;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should not allow admin to delete own account")
    void testDeleteUser_OwnAccount() throws Exception {
        mockMvc.perform(delete("/api/v1/admin/users/{id}", adminUser.getId())
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should not delete already deleted user")
    void testDeleteUser_AlreadyDeleted() throws Exception {
        testUser.setDeletedAt(java.time.LocalDateTime.now());
        userRepository.save(testUser);

        mockMvc.perform(delete("/api/v1/admin/users/{id}", testUser.getId())
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ==================== Change Role Tests ====================

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should change user role successfully")
    void testChangeUserRole() throws Exception {
        ChangeRoleRequest request = new ChangeRoleRequest();
        request.setRole(Role.ROLE_INTERVIEWER);

        mockMvc.perform(patch("/api/v1/admin/users/{id}/role", testUser.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Vai trò đã được cập nhật"));

        User updatedUser = userRepository.findById(testUser.getId()).get();
        assert updatedUser.getRole() == Role.ROLE_INTERVIEWER;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should not allow admin to change own role")
    void testChangeUserRole_OwnAccount() throws Exception {
        ChangeRoleRequest request = new ChangeRoleRequest();
        request.setRole(Role.ROLE_USER);

        mockMvc.perform(patch("/api/v1/admin/users/{id}/role", adminUser.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should not change role of deleted user")
    void testChangeUserRole_DeletedUser() throws Exception {
        testUser.setDeletedAt(java.time.LocalDateTime.now());
        userRepository.save(testUser);

        ChangeRoleRequest request = new ChangeRoleRequest();
        request.setRole(Role.ROLE_INTERVIEWER);

        mockMvc.perform(patch("/api/v1/admin/users/{id}/role", testUser.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ==================== Reset Password Tests ====================

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should reset password successfully")
    void testResetPassword() throws Exception {
        mockMvc.perform(post("/api/v1/admin/users/{id}/reset-password", testUser.getId())
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Email đặt lại mật khẩu đã được gửi"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should not reset password for deleted user")
    void testResetPassword_DeletedUser() throws Exception {
        testUser.setDeletedAt(java.time.LocalDateTime.now());
        userRepository.save(testUser);

        mockMvc.perform(post("/api/v1/admin/users/{id}/reset-password", testUser.getId())
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ==================== Audit Log Tests ====================

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get audit logs")
    void testGetAuditLogs() throws Exception {
        mockMvc.perform(get("/api/v1/admin/audit-logs")
                .param("page", "0")
                .param("size", "20")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    // ==================== Authorization Tests ====================

    @Test
    @DisplayName("Should deny access without authentication")
    void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should deny access for non-admin users")
    void testForbiddenAccess() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
}
