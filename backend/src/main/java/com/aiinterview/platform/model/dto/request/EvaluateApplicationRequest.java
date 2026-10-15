package com.aiinterview.platform.model.dto.request;

import com.aiinterview.platform.model.enums.ApplicationStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EvaluateApplicationRequest {

    @NotNull(message = "Trạng thái không được để trống")
    private ApplicationStatus status;

    @Min(0)
    @Max(100)
    private Integer score;

    private String feedback;
}
