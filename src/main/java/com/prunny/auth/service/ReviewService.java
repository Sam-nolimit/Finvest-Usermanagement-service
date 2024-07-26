package com.prunny.auth.service;

import com.prunny.auth.dto.response.ReviewResponse;
import com.prunny.auth.model.Review;

import java.util.List;

public interface ReviewService {
    ReviewResponse addReview(Long propertyId, Long tenantId, String comment, int rating);
    List<ReviewResponse> getReviewsByProperty(Long propertyId);
    void deleteReviewById(Long reviewId);
    List<ReviewResponse> getAllReviews();
}
