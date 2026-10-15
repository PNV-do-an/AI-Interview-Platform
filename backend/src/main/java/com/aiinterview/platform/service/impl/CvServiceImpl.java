package com.aiinterview.platform.service.impl;

import com.aiinterview.platform.model.dto.request.CvAnalyzeRequest;
import com.aiinterview.platform.model.dto.response.CvAnalysisResponse;
import com.aiinterview.platform.model.entity.CvDocument;
import com.aiinterview.platform.model.entity.User;
import com.aiinterview.platform.model.repository.CvDocumentRepository;
import com.aiinterview.platform.service.CvService;
import com.aiinterview.platform.service.ai.CvAiEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CvServiceImpl implements CvService {

    private final CvDocumentRepository cvRepository;
    private final CvAiEngine cvAiEngine;

    @Override
    @Transactional
    public CvAnalysisResponse analyze(User candidate, CvAnalyzeRequest request) {
        CvAiEngine.CvAnalysis analysis = cvAiEngine.analyze(request.getCvText());

        CvDocument doc = CvDocument.builder()
                .candidate(candidate)
                .fileName(request.getFileName() != null && !request.getFileName().isBlank()
                        ? request.getFileName() : "CV-" + System.currentTimeMillis())
                .content(request.getCvText())
                .detectedSkills(String.join(", ", analysis.skills()))
                .cvScore(analysis.score())
                .build();
        CvDocument saved = cvRepository.save(doc);
        return CvAnalysisResponse.from(saved, analysis.suggestions());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CvAnalysisResponse> getMyCvs(User candidate) {
        return cvRepository.findByCandidateOrderByCreatedAtDesc(candidate).stream()
                .map(doc -> CvAnalysisResponse.from(doc, List.of()))
                .toList();
    }
}
