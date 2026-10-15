package com.aiinterview.platform.controller;

import com.aiinterview.platform.common.response.ApiResponse;
import com.aiinterview.platform.model.dto.request.CvAnalyzeRequest;
import com.aiinterview.platform.model.dto.response.CvAnalysisResponse;
import com.aiinterview.platform.model.entity.User;
import com.aiinterview.platform.model.repository.UserRepository;
import com.aiinterview.platform.security.utils.SecurityUtils;
import com.aiinterview.platform.service.CvService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cv")
@RequiredArgsConstructor
public class CvController {

    private final CvService cvService;
    private final UserRepository userRepository;

    /** POST /api/v1/cv/analyze — AI analyzes CV text and returns skills/score/suggestions */
    @PostMapping("/analyze")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CvAnalysisResponse>> analyze(@Valid @RequestBody CvAnalyzeRequest request) {
        User candidate = SecurityUtils.getCurrentUser(userRepository);
        return ResponseEntity.ok(ApiResponse.success(cvService.analyze(candidate, request)));
    }

    /** GET /api/v1/cv/my — candidate's analyzed CVs */
    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<CvAnalysisResponse>>> getMyCvs() {
        User candidate = SecurityUtils.getCurrentUser(userRepository);
        return ResponseEntity.ok(ApiResponse.success(cvService.getMyCvs(candidate)));
    }
}
