package com.aiinterview.platform.model.repository;

import com.aiinterview.platform.model.entity.User;
import com.aiinterview.platform.model.enums.Role;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA implementation of UserRepository backed by MySQL.
 * Marked @Primary so Spring prefers this over FakeUserRepository when both are on the classpath.
 */
@Repository
@Primary
public interface JpaUserRepository extends JpaRepository<User, Long>, UserRepository {

    @Override
    Optional<User> findByEmail(String email);

    @Override
    boolean existsByEmail(String email);

    @Override
    Optional<User> findByVerificationToken(String verificationToken);

    @Override
    Optional<User> findByResetToken(String resetToken);

    /**
     * Admin list query: supports search by email/fullName, filter by status and role.
     * statusFilter values: "ACTIVE", "INACTIVE", "LOCKED" — handled via JPQL CASE logic.
     */
    @Override
    @Query("""
        SELECT u FROM User u
        WHERE (:search IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
                               OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')))
          AND (:role IS NULL OR u.role = :role)
          AND (
            :status IS NULL
            OR (:status = 'ACTIVE'   AND u.enabled = true  AND u.locked = false AND u.deletedAt IS NULL)
            OR (:status = 'INACTIVE' AND u.enabled = false AND u.deletedAt IS NULL)
            OR (:status = 'LOCKED'   AND u.locked = true   AND u.deletedAt IS NULL)
          )
        """)
    Page<User> findAllWithFilters(
            @Param("search") String search,
            @Param("role") Role role,
            @Param("status") String status,
            Pageable pageable
    );
}
