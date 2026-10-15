package com.aiinterview.platform.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReviewReplyRequest {

    @NotBlank(message = "Phản hồi không được để trống")
    private String reply;
}
