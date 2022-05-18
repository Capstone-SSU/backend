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

    public List<Review> findByLecture(Lecture lecture){
        List<Review> reviews = reviewRepository.findByLecture(lecture); // lecture 를 갖고 reviews 에 있는 모든 데이터 가져오기
        return reviews;
    }

    public void deleteReviews(Lecture lecture){
        reviewRepository.deleteReviews(lecture);
    }

    public void updateReview(ReviewPostDto reviewUpdateDto, Review review){
        int originalRate = review.getRate();
        int newRate = reviewUpdateDto.getRate();
        reviewRepository.updateReview(reviewUpdateDto, review.getReviewId());
        Lecture lecture = review.getLecture();
        List<Review> reviews = findByLecture(lecture);
        double newAvgRate = lecture.getAvgRate() - Math.round((double)(originalRate-newRate)/reviews.size()*10)/10.0;
        lecture.setAvgRate(newAvgRate);
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

