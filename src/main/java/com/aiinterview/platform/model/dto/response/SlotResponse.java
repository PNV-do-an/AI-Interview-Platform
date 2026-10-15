package com.aiinterview.platform.model.dto.response;

import com.aiinterview.platform.model.entity.InterviewSlot;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SlotResponse {

    private Long id;
    private Long employerId;
    private String employerName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean booked;
    private String bookedByName;
    private String note;
    private boolean cancelled;

    public static SlotResponse from(InterviewSlot slot) {
        return SlotResponse.builder()
                .id(slot.getId())
                .employerId(slot.getEmployer().getId())
                .employerName(slot.getEmployer().getFullName())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .booked(slot.getBookedBy() != null)
                .bookedByName(slot.getBookedBy() != null ? slot.getBookedBy().getFullName() : null)
                .note(slot.getNote())
                .cancelled(slot.isCancelled())
                .build();
    }
}
