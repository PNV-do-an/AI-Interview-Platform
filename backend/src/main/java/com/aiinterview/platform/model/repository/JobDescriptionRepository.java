package com.aiinterview.platform.model.repository;

import com.aiinterview.platform.model.entity.JobDescription;
import com.aiinterview.platform.model.entity.User;
import com.aiinterview.platform.model.enums.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JobDescriptionRepository extends JpaRepository<JobDescription, Long> {

    Optional<JobDescription> findByIdAndDeletedAtIsNull(Long id);

    Page<JobDescription> findByEmployerAndDeletedAtIsNull(User employer, Pageable pageable);

    @Query("""
            SELECT j FROM JobDescription j
            WHERE j.deletedAt IS NULL
              AND j.status = :status
              AND (:search IS NULL OR :search = ''
                   OR LOWER(j.title) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(j.companyName) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(j.position) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(j.location) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<JobDescription> searchOpenJobs(@Param("search") String search,
                                        @Param("status") JobStatus status,
                                        Pageable pageable);

    @Query("""
            SELECT j FROM JobDescription j
            WHERE j.deletedAt IS NULL
              AND (:search IS NULL OR :search = ''
                   OR LOWER(j.title) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(j.companyName) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<JobDescription> searchAllJobs(@Param("search") String search, Pageable pageable);
}
