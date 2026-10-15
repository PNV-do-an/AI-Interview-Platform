package com.aiinterview.platform.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CoverLetterGenerateRequest {

    /** Job the letter targets; optional (generic letter when absent) */
    private Long jobId;

    /** Existing analyzed CV to base the letter on */
    private Long cvId;

    @NotNull(message = "Tiêu đề thư không được để trống")
    private String title;
}
