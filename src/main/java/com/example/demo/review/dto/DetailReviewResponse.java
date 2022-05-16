package com.example.demo.review.dto;

import com.example.demo.lecture.Lecture;
import com.example.demo.lecture.dto.DetailLectureResponse;
import com.example.demo.review.Review;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class DetailReviewResponse {
    private String nickname;
    private long reviewId;
    private int rate;
    private String commentTitle;
    private String comment;
    private LocalDateTime createdDate;
    private boolean writerStatus;

    public static DetailReviewResponse from(Review review, Lecture lecture){
        return DetailReviewResponse.builder()
                .nickname(review.getUser().getUserNickname())
                .reviewId(review.getReviewId())
                .rate(review.getRate())
                .commentTitle(review.getCommentTitle())
                .comment(review.getComment())
                .createdDate(review.getCreatedDate())
                .writerStatus(false)
                .build();
    }
}
