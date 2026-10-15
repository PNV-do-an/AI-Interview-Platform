package com.aiinterview.platform.model.dto.response;

import com.aiinterview.platform.model.entity.Review;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewResponse {

    private Long id;
    private Long reviewerId;
    private String reviewerName;
    private Long targetId;
    private String targetName;
    private Long jobId;
    private String jobTitle;
    private Integer rating;
    private String comment;
    private String reply;
    private String repliedByName;
    private LocalDateTime repliedAt;
    private LocalDateTime createdAt;

    public static ReviewResponse from(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .reviewerId(review.getReviewer().getId())
                .reviewerName(review.getReviewer().getFullName())
                .targetId(review.getTarget().getId())
                .targetName(review.getTarget().getFullName())
                .jobId(review.getJob() != null ? review.getJob().getId() : null)
                .jobTitle(review.getJob() != null ? review.getJob().getTitle() : null)
                .rating(review.getRating())
                .comment(review.getComment())
                .reply(review.getReply())
                .repliedByName(review.getRepliedBy() != null ? review.getRepliedBy().getFullName() : null)
                .repliedAt(review.getRepliedAt())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
