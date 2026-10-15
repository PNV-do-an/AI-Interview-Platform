package com.aiinterview.platform.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JobRequest {

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    @NotBlank(message = "Tên công ty không được để trống")
    private String companyName;

    private String position;

    private String location;

    private String salaryRange;

    private String description;

    private String requirements;

    /** Comma-separated skills */
    private String requiredSkills;
}
