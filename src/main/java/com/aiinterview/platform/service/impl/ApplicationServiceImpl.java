package com.aiinterview.platform.service.impl;

import com.aiinterview.platform.common.exception.BadRequestException;
import com.aiinterview.platform.common.exception.ResourceNotFoundException;
import com.aiinterview.platform.model.dto.request.ApplyJobRequest;
import com.aiinterview.platform.model.dto.request.BookSlotRequest;
import com.aiinterview.platform.model.dto.request.EvaluateApplicationRequest;
import com.aiinterview.platform.model.dto.response.ApplicationResponse;
import com.aiinterview.platform.model.entity.Application;
import com.aiinterview.platform.model.entity.InterviewSlot;
import com.aiinterview.platform.model.entity.JobDescription;
import com.aiinterview.platform.model.entity.User;
import com.aiinterview.platform.model.enums.ApplicationStatus;
import com.aiinterview.platform.model.enums.JobStatus;
import com.aiinterview.platform.model.enums.Role;
import com.aiinterview.platform.model.repository.ApplicationRepository;
import com.aiinterview.platform.model.repository.InterviewSlotRepository;
import com.aiinterview.platform.model.repository.JobDescriptionRepository;
import com.aiinterview.platform.model.repository.UserRepository;
import com.aiinterview.platform.security.utils.SecurityUtils;
import com.aiinterview.platform.service.ApplicationService;
import com.aiinterview.platform.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobDescriptionRepository jobRepository;
    private final InterviewSlotRepository slotRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public ApplicationResponse apply(Long jobId, ApplyJobRequest request) {
        User candidate = SecurityUtils.getCurrentUser(userRepository);
        JobDescription job = jobRepository.findByIdAndDeletedAtIsNull(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tin tuyển dụng với id: " + jobId));
        if (job.getStatus() != JobStatus.OPEN) {
            throw new BadRequestException("Tin tuyển dụng này đã đóng");
        }
        boolean alreadyApplied = applicationRepository
                .existsByCandidateAndJobAndStatusNot(candidate, job, ApplicationStatus.CANCELLED);
        if (alreadyApplied) {
            throw new BadRequestException("Bạn đã ứng tuyển vào vị trí này");
        }
        Application application = Application.builder()
                .candidate(candidate)
                .job(job)
                .coverLetter(request != null ? request.getCoverLetter() : null)
                .status(ApplicationStatus.PENDING)
                .build();
        Application saved = applicationRepository.save(application);
        try {
            emailService.sendApplicationReceivedEmail(
                    job.getEmployer().getEmail(), candidate.getFullName(), job.getTitle());
        } catch (Exception e) {
            log.warn("Không gửi được email thông báo ứng tuyển", e);
        }
        return ApplicationResponse.from(saved);
    }

    @Override
    public Page<ApplicationResponse> getMyApplications(int page, int size) {
        User candidate = SecurityUtils.getCurrentUser(userRepository);
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return applicationRepository.findByCandidateOrderByCreatedAtDesc(candidate, pageable)
                .map(ApplicationResponse::from);
    }

    @Override
    @Transactional
    public void cancelApplication(Long applicationId) {
        User candidate = SecurityUtils.getCurrentUser(userRepository);
        Application application = applicationRepository.findByIdAndCandidate(applicationId, candidate)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn ứng tuyển với id: " + applicationId));
        if (application.getStatus() == ApplicationStatus.INTERVIEWED
                || application.getStatus() == ApplicationStatus.ACCEPTED
                || application.getStatus() == ApplicationStatus.REJECTED) {
            throw new BadRequestException("Không thể hủy đơn ở trạng thái " + application.getStatus());
        }
        application.setStatus(ApplicationStatus.CANCELLED);
        applicationRepository.save(application);
    }

    @Override
    public Page<ApplicationResponse> getJobApplications(Long jobId, int page, int size) {
        JobDescription job = findOwnedJobOrThrow(jobId);
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return applicationRepository.findByJobOrderByCreatedAtDesc(job, pageable)
                .map(ApplicationResponse::from);
    }

    @Override
    @Transactional
    public ApplicationResponse evaluate(Long applicationId, EvaluateApplicationRequest request) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn ứng tuyển với id: " + applicationId));
        User current = SecurityUtils.getCurrentUser(userRepository);
        boolean isOwner = application.getJob().getEmployer().getId().equals(current.getId());
        boolean isAdmin = current.getRole() == Role.ROLE_ADMIN || current.getRole() == Role.ROLE_STAFF;
        if (!isOwner && !isAdmin) {
            throw new BadRequestException("Bạn không có quyền đánh giá đơn ứng tuyển này");
        }
        validateTransition(application.getStatus(), request.getStatus());
        application.setStatus(request.getStatus());
        application.setScore(request.getScore());
        application.setFeedback(request.getFeedback());
        Application saved = applicationRepository.save(application);
        try {
            emailService.sendApplicationStatusEmail(
                    application.getCandidate().getEmail(),
                    application.getCandidate().getFullName(),
                    application.getJob().getTitle(),
                    request.getStatus().name());
        } catch (Exception e) {
            log.warn("Không gửi được email cập nhật trạng thái", e);
        }
        return ApplicationResponse.from(saved);
    }

    @Override
    @Transactional
    public ApplicationResponse bookSlot(BookSlotRequest request) {
        User candidate = SecurityUtils.getCurrentUser(userRepository);
        InterviewSlot slot = slotRepository.findByIdAndCancelledFalse(request.getSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khung giờ với id: " + request.getSlotId()));

        // re-check inside the transaction to prevent double booking
        if (slot.getBookedBy() != null) {
            throw new BadRequestException("Khung giờ này đã có người đặt");
        }
        if (slot.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Khung giờ này đã qua");
        }

        Application application;
        if (request.getApplicationId() != null) {
            application = applicationRepository.findByIdAndCandidate(request.getApplicationId(), candidate)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn ứng tuyển với id: " + request.getApplicationId()));
            if (!application.getJob().getEmployer().getId().equals(slot.getEmployer().getId())) {
                throw new BadRequestException("Khung giờ không thuộc người phỏng vấn của đơn này");
            }
        } else {
            // booking without a prior application is not allowed; the candidate must apply first
            throw new BadRequestException("Vui lòng ứng tuyển trước khi đặt lịch");
        }

        if (application.getStatus() != ApplicationStatus.PENDING && application.getStatus() != ApplicationStatus.SCHEDULED) {
            throw new BadRequestException("Đơn ứng tuyển không ở trạng thái có thể đặt lịch");
        }

        slot.setBookedBy(candidate);
        slot.setBookedAt(LocalDateTime.now());
        slotRepository.save(slot);

        application.setSlot(slot);
        application.setStatus(ApplicationStatus.SCHEDULED);
        Application saved = applicationRepository.save(application);

        String timeLabel = slot.getStartTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        try {
            emailService.sendSlotBookedEmail(candidate.getEmail(), candidate.getFullName(),
                    application.getJob().getTitle(), timeLabel, "Ứng viên");
            emailService.sendSlotBookedEmail(slot.getEmployer().getEmail(), slot.getEmployer().getFullName(),
                    application.getJob().getTitle(), timeLabel, "Người phỏng vấn");
        } catch (Exception e) {
            log.warn("Không gửi được email xác nhận lịch", e);
        }
        return ApplicationResponse.from(saved);
    }

    @Override
    public Page<ApplicationResponse> adminListApplications(int page, int size) {
        User current = SecurityUtils.getCurrentUser(userRepository);
        if (current.getRole() != Role.ROLE_ADMIN && current.getRole() != Role.ROLE_STAFF) {
            throw new BadRequestException("Không có quyền truy cập");
        }
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return applicationRepository.findAll(pageable).map(ApplicationResponse::from);
    }

    private void validateTransition(ApplicationStatus from, ApplicationStatus to) {
        if (to == null) {
            throw new BadRequestException("Trạng thái không hợp lệ");
        }
        boolean valid = switch (from) {
            case PENDING -> to == ApplicationStatus.SCHEDULED || to == ApplicationStatus.REJECTED;
            case SCHEDULED -> to == ApplicationStatus.INTERVIEWED || to == ApplicationStatus.REJECTED;
            case INTERVIEWED -> to == ApplicationStatus.ACCEPTED || to == ApplicationStatus.REJECTED;
            default -> false;
        };
        if (!valid) {
            throw new BadRequestException("Không thể chuyển trạng thái từ " + from + " sang " + to);
        }
    }

    private JobDescription findOwnedJobOrThrow(Long jobId) {
        User current = SecurityUtils.getCurrentUser(userRepository);
        JobDescription job = jobRepository.findByIdAndDeletedAtIsNull(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tin tuyển dụng với id: " + jobId));
        boolean isOwner = job.getEmployer().getId().equals(current.getId());
        boolean isAdmin = current.getRole() == Role.ROLE_ADMIN || current.getRole() == Role.ROLE_STAFF;
        if (!isOwner && !isAdmin) {
            throw new BadRequestException("Bạn không có quyền xem danh sách ứng viên của tin này");
        }
        return job;
    }
}
