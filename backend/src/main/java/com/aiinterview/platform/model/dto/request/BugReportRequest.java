package com.aiinterview.platform.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BugReportRequest {

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    @NotBlank(message = "Loại báo cáo không được để trống")
    private String category;

    @NotBlank(message = "Mô tả không được để trống")
    private String description;
}
