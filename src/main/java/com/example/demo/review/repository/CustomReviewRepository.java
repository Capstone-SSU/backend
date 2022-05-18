package com.example.demo.review.repository;

import com.example.demo.lecture.Lecture;
import com.example.demo.review.Review;
import com.example.demo.review.dto.ReviewPostDto;

import java.util.List;

public interface CustomReviewRepository {
    void updateReview(ReviewPostDto reviewUpdateDto, Long reviewId);
    void deleteReview(Long reviewId);
    List<Review> findByLecture(Lecture lecture);
    void deleteReviews(Lecture lecture);
}
