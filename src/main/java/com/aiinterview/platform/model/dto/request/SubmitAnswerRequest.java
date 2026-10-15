package com.aiinterview.platform.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubmitAnswerRequest {

    @NotBlank(message = "Câu trả lời không được để trống")
    private String answerText;

    private Integer durationSec;
}
