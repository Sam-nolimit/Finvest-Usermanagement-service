package com.prunny.auth.service.impl;

import com.prunny.auth.dto.response.ReviewResponse;
import com.prunny.auth.exception.ResourceNotFoundException;
import com.prunny.auth.model.Property;
import com.prunny.auth.model.Review;
import com.prunny.auth.model.User;
import com.prunny.auth.repository.PropertyRepository;
import com.prunny.auth.repository.ReviewRepository;
import com.prunny.auth.repository.UserRepository;
import com.prunny.auth.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ReviewResponse addReview(Long propertyId, Long tenantId, String comment, int rating) {
        try {
            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
            User tenant = userRepository.findById(tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));
            Review review = new Review();
            review.setProperty(property);
            review.setTenant(tenant);
            review.setComment(comment);
            review.setRating(rating);
            Review savedReview = reviewRepository.save(review);
            return new ReviewResponse(savedReview);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to add review due to unexpected error", e);
        }
    }

    @Override
    public List<ReviewResponse> getReviewsByProperty(Long propertyId) {
        try {
            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
            List<Review> reviews = reviewRepository.findByProperty(property);
            return reviews.stream().map(ReviewResponse::new).collect(Collectors.toList());
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get reviews by property due to unexpected error", e);
        }
    }

    @Override
    public List<ReviewResponse> getAllReviews() {
        try {
            List<Review> reviews = reviewRepository.findAll();
            return reviews.stream().map(ReviewResponse::new).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get all reviews due to unexpected error", e);
        }
    }

    @Override
    public void deleteReviewById(Long reviewId) {
        try {
            if (!reviewRepository.existsById(reviewId)) {
                throw new ResourceNotFoundException("Review not found");
            }
            reviewRepository.deleteById(reviewId);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete review due to unexpected error", e);
        }
    }
}
