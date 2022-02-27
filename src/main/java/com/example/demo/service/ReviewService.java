package com.example.demo.service;

import com.example.demo.domain.Lecture;
import com.example.demo.domain.Review;
import com.example.demo.domain.User;
import com.example.demo.dto.ReviewDto;
import com.example.demo.repository.LectureRepository;
import com.example.demo.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public long saveReview(Review review){
        Review savedReview = reviewRepository.save(review);
        return savedReview.getReviewId();
    }

    public Review findByReviewId(Long reviewId){
        Optional<Review> review = reviewRepository.findById(reviewId);
        return review.orElse(null);
    }

    public Review findByUserId(User user, Lecture lecture){ // fk 로 접근할 때 객체로 넘기자
        Optional<Review> review = reviewRepository.findByUserAndLecture(user, lecture);
        return review.orElse(null);
    }

    public void updateReview(ReviewDto reviewDto, Long reviewId){
        String commentTitle = reviewDto.getCommentTitle();
        String comment = reviewDto.getComment();
        reviewRepository.updateReview(commentTitle, comment, reviewId);
    }

    public void deleteReview(Long reviewId){
        reviewRepository.deleteReview(reviewId);
    }
}

