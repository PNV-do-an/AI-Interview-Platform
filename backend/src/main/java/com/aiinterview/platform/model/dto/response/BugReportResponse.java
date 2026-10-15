package com.aiinterview.platform.model.dto.response;

import com.aiinterview.platform.model.entity.BugReport;
import com.aiinterview.platform.model.enums.ReportCategory;
import com.aiinterview.platform.model.enums.ReportStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BugReportResponse {

    private Long id;
    private Long reporterId;
    private String reporterName;
    private String title;
    private ReportCategory category;
    private String description;
    private ReportStatus status;
    private String reply;
    private String repliedByName;
    private LocalDateTime repliedAt;
    private LocalDateTime createdAt;

    public static BugReportResponse from(BugReport report) {
        return BugReportResponse.builder()
                .id(report.getId())
                .reporterId(report.getReporter().getId())
                .reporterName(report.getReporter().getFullName())
                .title(report.getTitle())
                .category(report.getCategory())
                .description(report.getDescription())
                .status(report.getStatus())
                .reply(report.getReply())
                .repliedByName(report.getRepliedBy() != null ? report.getRepliedBy().getFullName() : null)
                .repliedAt(report.getRepliedAt())
                .createdAt(report.getCreatedAt())
                .build();
    }
}
