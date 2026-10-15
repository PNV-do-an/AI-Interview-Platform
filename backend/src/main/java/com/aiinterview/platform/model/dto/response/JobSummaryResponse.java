package com.aiinterview.platform.model.dto.response;

import com.aiinterview.platform.model.entity.JobDescription;
import com.aiinterview.platform.model.enums.JobStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class JobSummaryResponse {

    private Long id;
    private String title;
    private String companyName;
    private String position;
    private String location;
    private String salaryRange;
    private JobStatus status;
    private String employerName;
    private LocalDateTime createdAt;

    public static JobSummaryResponse from(JobDescription job) {
        return JobSummaryResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .companyName(job.getCompanyName())
                .position(job.getPosition())
                .location(job.getLocation())
                .salaryRange(job.getSalaryRange())
                .status(job.getStatus())
                .employerName(job.getEmployer().getFullName())
                .createdAt(job.getCreatedAt())
                .build();
    }
}
