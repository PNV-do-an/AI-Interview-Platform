package com.aiinterview.platform.model.repository;

import com.aiinterview.platform.model.entity.RegisterAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface RegisterAttemptRepository extends JpaRepository<RegisterAttempt, Long> {
    RegisterAttempt findFirstByAddressOrderByIdAsc(String ipClient);
    RegisterAttempt findFirstByAccountOrderByIdAsc(String email);
    long countByAddressAndTimestampAfter(String ip, LocalDateTime since);
}
