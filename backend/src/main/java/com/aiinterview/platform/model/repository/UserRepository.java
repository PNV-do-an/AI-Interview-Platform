package com.aiinterview.platform.model.repository;

import com.aiinterview.platform.model.entity.User;
import com.aiinterview.platform.model.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByVerificationToken(String verificationToken);
    Optional<User> findByResetToken(String resetToken);
    User save(User user);
    Optional<User> findById(Long id);
    void deleteAll();
    Page<User> findAllWithFilters(String search, Role role, String status, Pageable pageable);
}
