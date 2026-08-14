package com.aiinterview.platform.model.repository;

import com.aiinterview.platform.model.entity.LoginAttempt;
import java.time.LocalDateTime;
import java.util.List;

public interface LoginAttemptRepository {
    void addAttempt(LoginAttempt attempt);
    List<LoginAttempt> getFailedAttempts(String email, LocalDateTime since);
    long countFailedByIp(String ip, LocalDateTime since);
    List<LoginAttempt> getAllAttempts();
}
