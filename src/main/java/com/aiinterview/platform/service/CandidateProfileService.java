package com.aiinterview.platform.service;

import com.aiinterview.platform.model.dto.request.CandidateProfileRequest;
import com.aiinterview.platform.model.dto.response.CandidateProfileResponse;

public interface CandidateProfileService {

    CandidateProfileResponse getMyProfile();

    CandidateProfileResponse updateMyProfile(CandidateProfileRequest request);
}
