package com.aiinterview.platform.model.repository;

import com.aiinterview.platform.model.entity.LoginAttempt;
import java.util.List;

public interface LoginAttemptRepository {
    void addAttempt(LoginAttempt attempt);
    List<LoginAttempt> getFailedAttempts(String email, java.time.LocalDateTime since);
    List<LoginAttempt> getAllAttempts();
}
