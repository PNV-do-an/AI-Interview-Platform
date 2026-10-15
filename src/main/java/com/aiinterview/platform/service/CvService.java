package com.aiinterview.platform.service;

import com.aiinterview.platform.model.dto.request.CvAnalyzeRequest;
import com.aiinterview.platform.model.dto.response.CvAnalysisResponse;
import com.aiinterview.platform.model.entity.User;

import java.util.List;

public interface CvService {

    CvAnalysisResponse analyze(User candidate, CvAnalyzeRequest request);

    List<CvAnalysisResponse> getMyCvs(User candidate);
}
