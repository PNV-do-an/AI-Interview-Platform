package com.aiinterview.platform.service.impl;

import com.aiinterview.platform.common.exception.BadRequestException;
import com.aiinterview.platform.common.exception.ResourceNotFoundException;
import com.aiinterview.platform.model.dto.request.JobRequest;
import com.aiinterview.platform.model.dto.response.JobDetailResponse;
import com.aiinterview.platform.model.dto.response.JobSummaryResponse;
import com.aiinterview.platform.model.dto.response.MatchResponse;
import com.aiinterview.platform.model.entity.JobDescription;
import com.aiinterview.platform.model.entity.User;
import com.aiinterview.platform.model.enums.JobStatus;
import com.aiinterview.platform.model.enums.Role;
import com.aiinterview.platform.model.repository.JobDescriptionRepository;
import com.aiinterview.platform.model.repository.UserRepository;
import com.aiinterview.platform.security.utils.SecurityUtils;
import com.aiinterview.platform.service.JobService;
import com.aiinterview.platform.service.MatchingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobServiceImpl implements JobService {

    private final JobDescriptionRepository jobRepository;
    private final UserRepository userRepository;
    private final MatchingService matchingService;

    @Override
    public Page<JobSummaryResponse> searchJobs(int page, int size, String search) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        String searchParam = (search != null && !search.isBlank()) ? search.trim() : null;
        return jobRepository.searchOpenJobs(searchParam, JobStatus.OPEN, pageable)
                .map(JobSummaryResponse::from);
    }

    @Override
    public JobDetailResponse getJobDetail(Long jobId) {
        return JobDetailResponse.from(findJobOrThrow(jobId));
    }

    @Override
    public Page<JobSummaryResponse> getEmployerJobs(int page, int size) {
        User employer = SecurityUtils.getCurrentUser(userRepository);
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return jobRepository.findByEmployerAndDeletedAtIsNull(employer, pageable)
                .map(JobSummaryResponse::from);
    }

    @Override
    @Transactional
    public JobDetailResponse createJob(JobRequest request) {
        User employer = SecurityUtils.getCurrentUser(userRepository);
        JobDescription job = JobDescription.builder()
                .employer(employer)
                .title(request.getTitle())
                .companyName(request.getCompanyName())
                .position(request.getPosition())
                .location(request.getLocation())
                .salaryRange(request.getSalaryRange())
                .description(request.getDescription())
                .requirements(request.getRequirements())
                .requiredSkills(request.getRequiredSkills())
                .status(JobStatus.OPEN)
                .build();
        return JobDetailResponse.from(jobRepository.save(job));
    }

    @Override
    @Transactional
    public JobDetailResponse updateJob(Long jobId, JobRequest request) {
        JobDescription job = findOwnedJobOrThrow(jobId);
        job.setTitle(request.getTitle());
        job.setCompanyName(request.getCompanyName());
        job.setPosition(request.getPosition());
        job.setLocation(request.getLocation());
        job.setSalaryRange(request.getSalaryRange());
        job.setDescription(request.getDescription());
        job.setRequirements(request.getRequirements());
        job.setRequiredSkills(request.getRequiredSkills());
        return JobDetailResponse.from(jobRepository.save(job));
    }

    @Override
    @Transactional
    public JobDetailResponse changeStatus(Long jobId, JobStatus status) {
        if (status == null) {
            throw new BadRequestException("Trạng thái không hợp lệ");
        }
        JobDescription job = findOwnedJobOrThrow(jobId);
        job.setStatus(status);
        return JobDetailResponse.from(jobRepository.save(job));
    }

    @Override
    @Transactional
    public void deleteJob(Long jobId) {
        JobDescription job = findOwnedJobOrThrow(jobId);
        job.setDeletedAt(LocalDateTime.now());
        jobRepository.save(job);
    }

    @Override
    public Page<JobSummaryResponse> adminListJobs(int page, int size, String search) {
        User admin = SecurityUtils.getCurrentUser(userRepository);
        if (admin.getRole() != Role.ROLE_ADMIN && admin.getRole() != Role.ROLE_STAFF) {
            throw new BadRequestException("Không có quyền truy cập");
        }
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        String searchParam = (search != null && !search.isBlank()) ? search.trim() : null;
        return jobRepository.searchAllJobs(searchParam, pageable).map(JobSummaryResponse::from);
    }

    @Override
    public List<MatchResponse> getMatchingCandidates(Long jobId) {
        JobDescription job = findOwnedJobOrThrow(jobId);
        return matchingService.matchCandidates(job);
    }

    private JobDescription findJobOrThrow(Long jobId) {
        return jobRepository.findByIdAndDeletedAtIsNull(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tin tuyển dụng với id: " + jobId));
    }

    private JobDescription findOwnedJobOrThrow(Long jobId) {
        User current = SecurityUtils.getCurrentUser(userRepository);
        JobDescription job = findJobOrThrow(jobId);
        boolean isOwner = job.getEmployer().getId().equals(current.getId());
        boolean isAdmin = current.getRole() == Role.ROLE_ADMIN || current.getRole() == Role.ROLE_STAFF;
        if (!isOwner && !isAdmin) {
            throw new BadRequestException("Bạn không có quyền thao tác trên tin tuyển dụng này");
        }
        return job;
    }
}
