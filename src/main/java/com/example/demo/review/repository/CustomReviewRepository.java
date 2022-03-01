package com.example.demo.review.repository;

import com.example.demo.lecture.Lecture;
import com.example.demo.review.Review;

import java.util.List;

public interface CustomReviewRepository {
    void updateReview(String commentTitle, String comment, Long reviewId);
    void deleteReview(Long reviewId);
    List<Review> findByLecture(Lecture lecture);
}
