package com.aiinterview.platform.model.dto.response;

import com.aiinterview.platform.model.entity.CoverLetter;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CoverLetterResponse {

    private Long id;
    private String title;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CoverLetterResponse from(CoverLetter cl) {
        return CoverLetterResponse.builder()
                .id(cl.getId())
                .title(cl.getTitle())
                .jobId(cl.getJob() != null ? cl.getJob().getId() : null)
                .jobTitle(cl.getJob() != null ? cl.getJob().getTitle() : null)
                .companyName(cl.getJob() != null ? cl.getJob().getCompanyName() : null)
                .content(cl.getContent())
                .createdAt(cl.getCreatedAt())
                .updatedAt(cl.getUpdatedAt())
                .build();
    }
}
