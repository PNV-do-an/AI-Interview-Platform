package com.aiinterview.platform.model.repository;

import com.aiinterview.platform.model.entity.CoverLetter;
import com.aiinterview.platform.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoverLetterRepository extends JpaRepository<CoverLetter, Long> {

    List<CoverLetter> findByCandidateOrderByUpdatedAtDesc(User candidate);
}
