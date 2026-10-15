package com.aiinterview.platform.controller;

import com.aiinterview.platform.common.response.ApiResponse;
import com.aiinterview.platform.model.dto.request.ApplyJobRequest;
import com.aiinterview.platform.model.dto.request.BookSlotRequest;
import com.aiinterview.platform.model.dto.request.EvaluateApplicationRequest;
import com.aiinterview.platform.model.dto.response.ApplicationResponse;
import com.aiinterview.platform.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    /** POST /api/v1/applications/jobs/{jobId} — candidate applies */
    @PostMapping("/jobs/{jobId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<ApplicationResponse>> apply(
            @PathVariable Long jobId, @RequestBody(required = false) ApplyJobRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Ứng tuyển thành công", applicationService.apply(jobId, request)));
    }

    /** GET /api/v1/applications/my — candidate's own applications */
    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Page<ApplicationResponse>>> getMyApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(applicationService.getMyApplications(page, size)));
    }

    /** PATCH /api/v1/applications/{id}/cancel */
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> cancel(@PathVariable Long id) {
        applicationService.cancelApplication(id);
        return ResponseEntity.ok(ApiResponse.success("Đã hủy đơn ứng tuyển", null));
    }

    /** POST /api/v1/applications/book-slot — candidate books an available slot */
    @PostMapping("/book-slot")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<ApplicationResponse>> bookSlot(@Valid @RequestBody BookSlotRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Đặt lịch thành công", applicationService.bookSlot(request)));
    }

    /** GET /api/v1/applications/jobs/{jobId} — employer views applications for a job */
    @GetMapping("/jobs/{jobId}")
    @PreAuthorize("hasAnyRole('INTERVIEWER','ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<Page<ApplicationResponse>>> getJobApplications(
            @PathVariable Long jobId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(applicationService.getJobApplications(jobId, page, size)));
    }

    /** PATCH /api/v1/applications/{id}/evaluate — employer evaluates and scores */
    @PatchMapping("/{id}/evaluate")
    @PreAuthorize("hasAnyRole('INTERVIEWER','ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<ApplicationResponse>> evaluate(
            @PathVariable Long id, @Valid @RequestBody EvaluateApplicationRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Đã cập nhật đánh giá", applicationService.evaluate(id, request)));
    }

    /** GET /api/v1/applications/admin — admin/staff lists all applications */
    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<Page<ApplicationResponse>>> adminList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(applicationService.adminListApplications(page, size)));
    }
}
