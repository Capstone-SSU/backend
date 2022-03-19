package com.example.demo.mypage.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyReviewsResponse {
    private long lectureId;
    private String thumbnailUrl;
    private String lectureTitle;
    private double avgRate; // 별점 평균
    private String commentTitle;
    private String comment;

    @Builder
    public MyReviewsResponse(long lectureId, String thumbnailUrl, String lectureTitle, double avgRate, String commentTitle, String comment) {
        this.lectureId = lectureId;
        this.thumbnailUrl = thumbnailUrl;
        this.lectureTitle = lectureTitle;
        this.avgRate = avgRate;
        this.commentTitle = commentTitle;
        this.comment = comment;
    }
}
