package com.aiinterview.platform.model.dto.response;

import com.aiinterview.platform.model.entity.JobDescription;
import com.aiinterview.platform.model.enums.JobStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class JobDetailResponse {

    private Long id;
    private String title;
    private String companyName;
    private String position;
    private String location;
    private String salaryRange;
    private String description;
    private String requirements;
    private String requiredSkills;
    private JobStatus status;
    private Long employerId;
    private String employerName;
    private String employerEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static JobDetailResponse from(JobDescription job) {
        return JobDetailResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .companyName(job.getCompanyName())
                .position(job.getPosition())
                .location(job.getLocation())
                .salaryRange(job.getSalaryRange())
                .description(job.getDescription())
                .requirements(job.getRequirements())
                .requiredSkills(job.getRequiredSkills())
                .status(job.getStatus())
                .employerId(job.getEmployer().getId())
                .employerName(job.getEmployer().getFullName())
                .employerEmail(job.getEmployer().getEmail())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }
}
