package com.example.demo.review.repository;

import com.example.demo.lecture.Lecture;
import com.example.demo.review.Review;
import com.example.demo.review.dto.ReviewPostDto;
import com.example.demo.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface CustomReviewRepository {
    void updateReview(ReviewPostDto reviewUpdateDto, Long reviewId);
    void deleteReview(Long reviewId);
    List<Review> findByLecture(Lecture lecture);
    void deleteReviews(Lecture lecture);
    Optional<Review> findByUserAndLecture(User user, Lecture lecture);
}
