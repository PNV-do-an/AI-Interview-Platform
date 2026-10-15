package com.aiinterview.platform.model.dto.response;

import com.aiinterview.platform.model.entity.CandidateProfile;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CandidateProfileResponse {

    private Long id;
    private String skills;
    private Integer experienceYears;
    private String summary;
    private String careerGoal;
    private String cvFileName;
    private LocalDateTime updatedAt;

    public static CandidateProfileResponse from(CandidateProfile profile) {
        if (profile == null) {
            return null;
        }
        return CandidateProfileResponse.builder()
                .id(profile.getId())
                .skills(profile.getSkills())
                .experienceYears(profile.getExperienceYears())
                .summary(profile.getSummary())
                .careerGoal(profile.getCareerGoal())
                .cvFileName(profile.getCvFileName())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
