package com.aiinterview.platform.model.repository;

import com.aiinterview.platform.model.entity.CandidateProfile;
import com.aiinterview.platform.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CandidateProfileRepository extends JpaRepository<CandidateProfile, Long> {

    Optional<CandidateProfile> findByUser(User user);
}
