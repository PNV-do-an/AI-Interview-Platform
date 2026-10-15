package com.aiinterview.platform.service;

import com.aiinterview.platform.model.dto.request.ApplyJobRequest;
import com.aiinterview.platform.model.dto.request.BookSlotRequest;
import com.aiinterview.platform.model.dto.request.EvaluateApplicationRequest;
import com.aiinterview.platform.model.dto.response.ApplicationResponse;
import org.springframework.data.domain.Page;

public interface ApplicationService {

    ApplicationResponse apply(Long jobId, ApplyJobRequest request);

    Page<ApplicationResponse> getMyApplications(int page, int size);

    void cancelApplication(Long applicationId);

    Page<ApplicationResponse> getJobApplications(Long jobId, int page, int size);

    ApplicationResponse evaluate(Long applicationId, EvaluateApplicationRequest request);

    ApplicationResponse bookSlot(BookSlotRequest request);

    Page<ApplicationResponse> adminListApplications(int page, int size);
}
