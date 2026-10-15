package com.aiinterview.platform.model.repository;

import com.aiinterview.platform.model.entity.CvDocument;
import com.aiinterview.platform.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CvDocumentRepository extends JpaRepository<CvDocument, Long> {

    List<CvDocument> findByCandidateOrderByCreatedAtDesc(User candidate);
}
