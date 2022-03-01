package com.example.demo.review;

import com.example.demo.lecture.Lecture;
import com.example.demo.user.User;
import com.example.demo.review.dto.UpdateReviewDto;
import com.example.demo.review.repository.ReviewRepository;
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
    
    public long saveReviewOnLecture(User user, Lecture lecture){
        System.out.println(user.getUserEmail());
        System.out.println("lecture.getLectureUrl() = " + lecture.getLectureUrl());
        return 1;
//        Review savedReview = reviewRepository.save(review);
//        return savedReview.getReviewId();
    }
    
    
    public Review findByReviewId(Long reviewId){
        Optional<Review> review = reviewRepository.findById(reviewId);
        return review.orElse(null);
    }

    public Review findByUserId(User user, Lecture lecture){ // fk 로 접근할 때 객체로 넘기자
        Optional<Review> review = reviewRepository.findByUserAndLecture(user, lecture);
        return review.orElse(null);
    }

    public void updateReview(UpdateReviewDto updateReviewDto, Long reviewId){
        String commentTitle = updateReviewDto.getCommentTitle();
        String comment = updateReviewDto.getComment();
        reviewRepository.updateReview(commentTitle, comment, reviewId);
    }

    public void deleteReview(Long reviewId){
        reviewRepository.deleteReview(reviewId);
    }
}

