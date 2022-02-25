package com.example.demo.service;

import com.example.demo.domain.Review;
import com.example.demo.domain.ReviewHashtag;
import com.example.demo.repository.ReviewHashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewHashtagService {
    private final ReviewHashtagRepository reviewHashtagRepository;

    public long saveReviewHashtag(ReviewHashtag reviewHashtag){
        ReviewHashtag savedReviewHashtag = reviewHashtagRepository.save(reviewHashtag);
        return savedReviewHashtag.getReviewTagId();
    }

}
