package com.aiinterview.platform.model.dto.request;

import lombok.Data;

@Data
public class CandidateProfileRequest {

    /** Comma-separated skills */
    private String skills;

    private Integer experienceYears;

    private String summary;

    private String careerGoal;

    private String cvFileName;
}
