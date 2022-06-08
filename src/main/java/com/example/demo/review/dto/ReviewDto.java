package com.example.demo.review.dto;

import com.example.demo.lecture.Lecture;
import com.example.demo.review.Review;
import com.example.demo.user.domain.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewDto {
    private String lectureUrl;
    private String comment;
    private String commentTitle;
    private int rate;

    public Review toEntity(User user, Lecture lecture){
        return Review.builder()
                .user(user)
                .lecture(lecture)
                .rate(rate)
                .commentTitle(commentTitle)
                .comment(comment)
                .createdDate(LocalDateTime.now())
                .reviewStatus(1)
                .build();
    }
}
