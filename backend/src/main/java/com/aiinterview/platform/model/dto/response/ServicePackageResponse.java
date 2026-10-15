package com.aiinterview.platform.model.dto.response;

import com.aiinterview.platform.model.entity.ServicePackage;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ServicePackageResponse {

    private Long id;
    private String name;
    private String description;
    private Long price;
    private Integer durationDays;
    private Integer jobPostLimit;
    private List<String> features;
    private Boolean active;
    private LocalDateTime createdAt;

    public static ServicePackageResponse from(ServicePackage pkg) {
        return ServicePackageResponse.builder()
                .id(pkg.getId())
                .name(pkg.getName())
                .description(pkg.getDescription())
                .price(pkg.getPrice())
                .durationDays(pkg.getDurationDays())
                .jobPostLimit(pkg.getJobPostLimit())
                .features(pkg.getFeatures())
                .active(pkg.getActive())
                .createdAt(pkg.getCreatedAt())
                .build();
    }
}
