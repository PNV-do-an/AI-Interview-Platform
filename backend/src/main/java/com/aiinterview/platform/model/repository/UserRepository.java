package com.aiinterview.platform.model.repository;

import com.aiinterview.platform.model.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByVerificationToken(String verificationToken);
    Optional<User> findByResetToken(String resetToken);
    User save(User user);
    Optional<User> findById(Long id);
    Boolean addUser(User user);
    Long generateIdUser();
}
