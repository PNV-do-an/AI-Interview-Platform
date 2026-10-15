package com.aiinterview.platform.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cv_documents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CvDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private User candidate;

    private String fileName;

    /** Raw extracted CV text (pasted or from upload) */
    @Column(length = 20000)
    private String content;

    /** Comma-separated skills detected by AI analysis */
    private String detectedSkills;

    @Column(nullable = false)
    @Builder.Default
    private int cvScore = 0;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
