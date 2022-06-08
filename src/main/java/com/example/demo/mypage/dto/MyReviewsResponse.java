package com.example.demo.mypage.dto;

import com.example.demo.lecture.Lecture;
import com.example.demo.review.Review;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MyReviewsResponse {
    private long lectureId;
    private String thumbnailUrl;
    private String lectureTitle;
    private double avgRate; // 별점 평균
    private String commentTitle;
    private String comment;

    public static MyReviewsResponse from(Review review, Lecture lecture) {
        return MyReviewsResponse.builder()
                .lectureId(lecture.getLectureId())
                .thumbnailUrl(lecture.getThumbnailUrl())
                .lectureTitle(lecture.getLectureTitle())
                .avgRate(lecture.getAvgRate())
                .commentTitle(review.getCommentTitle())
                .comment(review.getComment())
                .build();
    }
}
