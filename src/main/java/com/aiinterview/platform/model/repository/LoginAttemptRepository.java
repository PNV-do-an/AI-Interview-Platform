package com.aiinterview.platform.model.repository;

import com.aiinterview.platform.model.entity.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {
    List<LoginAttempt> findByEmailAndSuccessFalseAndTimestampAfter(String email, LocalDateTime since);
    long countByIpAddressAndSuccessFalseAndTimestampAfter(String ip, LocalDateTime since);
}
