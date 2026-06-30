package com.hoc.backend.SpringBoot.repository;

import com.hoc.backend.SpringBoot.model.LoginAttempt;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class FakeLoginAttemptRepository implements LoginAttemptRepository {

    private final ArrayList<LoginAttempt> attempts = new ArrayList<>();

    @Override
    public void addAttempt(LoginAttempt attempt) {
        attempts.add(attempt);
    }

    @Override
    public List<LoginAttempt> getFailedAttempts(String email, LocalDateTime since) {
        return attempts.stream()
                .filter(a -> a.getEmail().equals(email))
                .filter(a -> !a.isSuccess())
                .filter(a -> a.getTimestamp().isAfter(since))
                .collect(Collectors.toList());
    }

    @Override
    public List<LoginAttempt> getAllAttempts() {
        return new ArrayList<>(attempts);
    }
}
