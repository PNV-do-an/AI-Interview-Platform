package com.aiinterview.platform.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReviewListResponse {

    private Double averageRating;
    private long totalReviews;
    private List<ReviewResponse> reviews;

    public static ReviewListResponse of(List<ReviewResponse> reviews, Double averageRating, long totalReviews) {
        return ReviewListResponse.builder()
                .averageRating(averageRating)
                .totalReviews(totalReviews)
                .reviews(reviews)
                .build();
    }
}
