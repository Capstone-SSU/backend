package com.example.demo.reviewHashtag;

import com.example.demo.hashtag.Hashtag;
import com.example.demo.lecture.Lecture;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewHashtagService {
    private final ReviewHashtagRepository reviewHashtagRepository;
    public long saveReviewHashtag(ReviewHashtag reviewHashtag){
        ReviewHashtag savedReviewHashtag = reviewHashtagRepository.save(reviewHashtag);
        return savedReviewHashtag.getReviewTagId();
    }

//    public List<Hashtag> getHashtags(Lecture lecture){
//        List<ReviewHashtag> hashtags = reviewHashtagRepository.findByLecture()
//    }

}
