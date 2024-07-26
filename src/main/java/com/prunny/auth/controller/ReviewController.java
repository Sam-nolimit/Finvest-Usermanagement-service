package com.prunny.auth.controller;

import com.prunny.auth.dto.request.ReviewRequest;
import com.prunny.auth.dto.response.ReviewResponse;
import com.prunny.auth.model.Review;
import com.prunny.auth.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> addReview(@RequestParam Long propertyId, @RequestParam Long tenantId, @RequestBody ReviewRequest reviewRequest) {
        ReviewResponse newReview = reviewService.addReview(propertyId, tenantId, reviewRequest.getComment(), reviewRequest.getRating());
        return ResponseEntity.status(HttpStatus.CREATED).body(newReview);
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByProperty(@PathVariable Long propertyId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByProperty(propertyId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getAllReviews() {
        List<ReviewResponse> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReviewById(@PathVariable Long reviewId) {
        reviewService.deleteReviewById(reviewId);
        return ResponseEntity.noContent().build();
    }
}
