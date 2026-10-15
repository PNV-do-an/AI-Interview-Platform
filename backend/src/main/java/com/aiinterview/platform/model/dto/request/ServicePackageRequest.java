package com.aiinterview.platform.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ServicePackageRequest {

    @NotBlank(message = "Tên gói không được để trống")
    private String name;

    private String description;

    @NotNull(message = "Giá gói không được để trống")
    @Min(value = 0, message = "Giá gói phải lớn hơn hoặc bằng 0")
    private Long price;

    @NotNull(message = "Thời hạn gói không được để trống")
    @Min(value = 1, message = "Thời hạn gói phải ít nhất 1 ngày")
    private Integer durationDays;

    @NotNull(message = "Giới hạn tin đăng không được để trống")
    @Min(value = 1, message = "Giới hạn tin đăng phải ít nhất 1")
    private Integer jobPostLimit;

    private List<String> features;
}
