package com.aiinterview.platform.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class InterviewResultResponse {

    private InterviewSessionResponse session;
    private List<InterviewAnswerResponse> answers;
}
