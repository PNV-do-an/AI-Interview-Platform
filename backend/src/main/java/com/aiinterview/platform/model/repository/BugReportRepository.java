package com.aiinterview.platform.model.repository;

import com.aiinterview.platform.model.entity.BugReport;
import com.aiinterview.platform.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BugReportRepository extends JpaRepository<BugReport, Long> {

    List<BugReport> findByReporterOrderByCreatedAtDesc(User reporter);
}
