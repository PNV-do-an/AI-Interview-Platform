package com.aiinterview.platform.model.repository;

import com.aiinterview.platform.model.entity.Review;
import com.aiinterview.platform.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByTargetOrderByCreatedAtDesc(User target);

    List<Review> findByReviewerOrderByCreatedAtDesc(User reviewer);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.target = :target")
    Double averageRatingByTarget(@Param("target") User target);

    long countByTarget(User target);
}
