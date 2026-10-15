package com.aiinterview.platform.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookSlotRequest {

    @NotNull(message = "Khung giờ không được để trống")
    private Long slotId;

    /** Optional: link booking to an existing application */
    private Long applicationId;
}
