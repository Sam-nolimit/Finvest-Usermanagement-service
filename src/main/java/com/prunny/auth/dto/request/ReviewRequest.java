package com.prunny.auth.dto.request;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ReviewRequest {
    private String comment;
    private int rating;


}