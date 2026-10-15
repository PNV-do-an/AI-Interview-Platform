package com.aiinterview.platform.model.dto.response;

import com.aiinterview.platform.model.entity.Application;
import com.aiinterview.platform.model.enums.ApplicationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ApplicationResponse {

    private Long id;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private Long employerId;
    private Long candidateId;
    private String candidateName;
    private String candidateEmail;
    private ApplicationStatus status;
    private Integer score;
    private String feedback;
    private String coverLetter;
    private SlotResponse slot;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ApplicationResponse from(Application application) {
        return ApplicationResponse.builder()
                .id(application.getId())
                .jobId(application.getJob().getId())
                .jobTitle(application.getJob().getTitle())
                .companyName(application.getJob().getCompanyName())
                .employerId(application.getJob().getEmployer().getId())
                .candidateId(application.getCandidate().getId())
                .candidateName(application.getCandidate().getFullName())
                .candidateEmail(application.getCandidate().getEmail())
                .status(application.getStatus())
                .score(application.getScore())
                .feedback(application.getFeedback())
                .coverLetter(application.getCoverLetter())
                .slot(application.getSlot() != null ? SlotResponse.from(application.getSlot()) : null)
                .createdAt(application.getCreatedAt())
                .updatedAt(application.getUpdatedAt())
                .build();
    }
}
