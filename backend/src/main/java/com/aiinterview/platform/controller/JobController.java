package com.aiinterview.platform.controller;

import com.aiinterview.platform.common.response.ApiResponse;
import com.aiinterview.platform.model.dto.request.JobRequest;
import com.aiinterview.platform.model.dto.response.JobDetailResponse;
import com.aiinterview.platform.model.dto.response.JobSummaryResponse;
import com.aiinterview.platform.model.dto.response.MatchResponse;
import com.aiinterview.platform.model.enums.JobStatus;
import com.aiinterview.platform.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    /** GET /api/v1/jobs?page=0&size=10&search=java */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<JobSummaryResponse>>> searchJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.success(jobService.searchJobs(page, size, search)));
    }

    /** GET /api/v1/jobs/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobDetailResponse>> getJobDetail(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(jobService.getJobDetail(id)));
    }

    /** GET /api/v1/jobs/my-jobs — employer's own jobs */
    @GetMapping("/my-jobs")
    @PreAuthorize("hasAnyRole('INTERVIEWER','ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<Page<JobSummaryResponse>>> getMyJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(jobService.getEmployerJobs(page, size)));
    }

    /** POST /api/v1/jobs */
    @PostMapping
    @PreAuthorize("hasAnyRole('INTERVIEWER','ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<JobDetailResponse>> createJob(@Valid @RequestBody JobRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Tin tuyển dụng đã được tạo", jobService.createJob(request)));
    }

    /** PUT /api/v1/jobs/{id} */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('INTERVIEWER','ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<JobDetailResponse>> updateJob(
            @PathVariable Long id, @Valid @RequestBody JobRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Tin tuyển dụng đã được cập nhật", jobService.updateJob(id, request)));
    }

    /** PATCH /api/v1/jobs/{id}/status?status=OPEN|CLOSED */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('INTERVIEWER','ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<JobDetailResponse>> changeStatus(
            @PathVariable Long id, @RequestParam JobStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Trạng thái đã được cập nhật", jobService.changeStatus(id, status)));
    }

    /** DELETE /api/v1/jobs/{id} */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('INTERVIEWER','ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<Void>> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return ResponseEntity.ok(ApiResponse.success("Tin tuyển dụng đã được xóa", null));
    }

    /** GET /api/v1/jobs/{id}/suggestions — matching candidates (owner or admin) */
    @GetMapping("/{id}/suggestions")
    @PreAuthorize("hasAnyRole('INTERVIEWER','ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<MatchResponse>>> getMatchingCandidates(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(jobService.getMatchingCandidates(id)));
    }
}
