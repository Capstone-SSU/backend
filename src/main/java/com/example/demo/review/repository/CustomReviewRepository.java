package com.example.demo.review.repository;

public interface CustomReviewRepository {
    void updateReview(String commentTitle, String comment, Long reviewId);
    void deleteReview(Long reviewId);
}
