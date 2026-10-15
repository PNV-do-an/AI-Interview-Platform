package com.aiinterview.platform.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequest {

    @NotNull(message = "Gói dịch vụ không được để trống")
    private Long packageId;

    @NotBlank(message = "Phương thức thanh toán không được để trống")
    private String paymentMethod;
}
