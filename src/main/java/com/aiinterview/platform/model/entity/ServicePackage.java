package com.aiinterview.platform.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "service_packages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicePackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 2000)
    private String description;

    /** Giá gói (VND) */
    @Column(nullable = false)
    private Long price;

    /** Thời hạn sử dụng sau khi thanh toán (số ngày) */
    @Column(nullable = false)
    private Integer durationDays;

    /** Số tin tuyển dụng được đăng trong thời hạn gói */
    @Column(nullable = false)
    private Integer jobPostLimit;

    @ElementCollection
    @CollectionTable(name = "service_package_features", joinColumns = @JoinColumn(name = "package_id"))
    @Column(name = "feature", length = 500)
    @OrderColumn(name = "idx")
    @Builder.Default
    private List<String> features = new ArrayList<>();

    @Builder.Default
    private Boolean active = true;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (active == null) {
            active = true;
        }
    }
}
