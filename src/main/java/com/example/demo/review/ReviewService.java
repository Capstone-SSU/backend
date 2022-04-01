package com.example.demo.review;

import com.example.demo.lecture.Lecture;
import com.example.demo.review.dto.ReviewPostDto;
import com.example.demo.user.User;
import com.example.demo.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public void saveReview(Review review){
        reviewRepository.save(review);
    }
    
    public Review findByReviewId(Long reviewId){
        Optional<Review> review = reviewRepository.findById(reviewId);
        return review.orElse(null);
    }

    public Review findByUserAndLecture(User user, Lecture lecture){ // fk 로 접근할 때 객체로 넘기자
        Optional<Review> review = reviewRepository.findByUserAndLecture(user, lecture);
        return review.orElse(null);
    }

    public void updateReview(ReviewPostDto reviewUpdateDto, Long reviewId){
        reviewRepository.updateReview(reviewUpdateDto, reviewId);
    }

    public void deleteReview(Long reviewId){
        reviewRepository.deleteReview(reviewId);
    }

    public List<Review> findAllReviewsByUser(User user){
        List<Review> reviews=reviewRepository.findByUser(user);
        reviews.removeIf(review -> review.getReviewStatus() == 0);
        return reviews;
    }
}

