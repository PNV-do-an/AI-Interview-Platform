package com.hoc.backend.SpringBoot.repository;

import com.hoc.backend.SpringBoot.model.LoginAttempt;
import java.util.List;

public interface LoginAttemptRepository {
    void addAttempt(LoginAttempt attempt);
    List<LoginAttempt> getFailedAttempts(String email, java.time.LocalDateTime since);
    List<LoginAttempt> getAllAttempts();
}
