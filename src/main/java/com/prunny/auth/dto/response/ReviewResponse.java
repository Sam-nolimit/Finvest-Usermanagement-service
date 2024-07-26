package com.prunny.auth.dto.response;

import com.prunny.auth.model.Review;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ReviewResponse {
    private Long id;
    private String content;
    private String tenantName;

public ReviewResponse(Review review) {
    this.id = review.getId();
    this.content = review.getComment();
    this.tenantName = review.getTenant().getFirstName() + " " + review.getTenant().getLastName();
}
    // Getters and Setters
}
