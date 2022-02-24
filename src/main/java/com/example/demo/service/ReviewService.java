package com.example.demo.service;

import com.example.demo.domain.Lecture;
import com.example.demo.domain.Review;
import com.example.demo.repository.LectureRepository;
import com.example.demo.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public long saveReview(Review review){
        Review savedReview = reviewRepository.save(review);
        return savedReview.getReviewId();
    }

}

