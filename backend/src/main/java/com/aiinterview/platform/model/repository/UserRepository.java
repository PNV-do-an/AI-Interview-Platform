package com.aiinterview.platform.model.repository;

import com.aiinterview.platform.model.entity.User;
import com.aiinterview.platform.model.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Custom repository interface for User.
 * Implementations: FakeUserRepository (in-memory) and JpaUserRepository (JPA/DB).
 */
public interface UserRepository {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByVerificationToken(String verificationToken);

    Optional<User> findByResetToken(String resetToken);

    User save(User user);

    Optional<User> findById(Long id);

    void deleteAll();

    /**
     * Admin list query: supports search by email/fullName, filter by status and role.
     * statusFilter values: "ACTIVE", "INACTIVE", "LOCKED"
     */
    Page<User> findAllWithFilters(String search, Role role, String status, Pageable pageable);
}
