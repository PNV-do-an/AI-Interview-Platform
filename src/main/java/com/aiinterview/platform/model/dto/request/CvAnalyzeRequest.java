package com.aiinterview.platform.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CvAnalyzeRequest {

    @NotBlank(message = "Nội dung CV không được để trống")
    private String cvText;

    private String fileName;
}
