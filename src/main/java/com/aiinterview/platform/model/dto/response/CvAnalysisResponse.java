package com.aiinterview.platform.model.dto.response;

import com.aiinterview.platform.model.entity.CvDocument;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CvAnalysisResponse {

    private Long id;
    private String fileName;
    private List<String> detectedSkills;
    private int cvScore;
    private List<String> suggestions;
    private LocalDateTime createdAt;

    public static CvAnalysisResponse from(CvDocument doc, List<String> suggestions) {
        return CvAnalysisResponse.builder()
                .id(doc.getId())
                .fileName(doc.getFileName())
                .detectedSkills(doc.getDetectedSkills() == null ? List.of()
                        : List.of(doc.getDetectedSkills().split(",\\s*")))
                .cvScore(doc.getCvScore())
                .suggestions(suggestions)
                .createdAt(doc.getCreatedAt())
                .build();
    }
}
