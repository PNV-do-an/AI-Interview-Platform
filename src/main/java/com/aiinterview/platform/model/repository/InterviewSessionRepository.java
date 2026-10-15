package com.aiinterview.platform.model.repository;

import com.aiinterview.platform.model.entity.InterviewSession;
import com.aiinterview.platform.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewSessionRepository extends JpaRepository<InterviewSession, Long> {

    Page<InterviewSession> findByCandidateOrderByCreatedAtDesc(User candidate, Pageable pageable);
}
