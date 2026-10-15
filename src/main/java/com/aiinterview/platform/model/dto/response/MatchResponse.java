package com.aiinterview.platform.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MatchResponse {

    private Long candidateId;
    private String fullName;
    private String email;
    private String skills;
    private Integer experienceYears;
    /** 0-100 */
    private int matchScore;
    private List<String> matchedSkills;
}
