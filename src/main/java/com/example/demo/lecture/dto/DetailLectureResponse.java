package com.example.demo.lecture.dto;

import com.example.demo.lecture.Lecture;
import com.example.demo.review.dto.DetailReviewResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Builder
public class DetailLectureResponse {
    private long lectureId;
    private String lectureTitle;
    private String lecturer;
    private String siteName;
    private String lectureUrl;
    private String thumbnailUrl;
    private double avgRate; // 별점 평균
    private List<String> hashtags; // 해시태그들
    private int reviewCnt; // 리뷰 개수
    private int likeCnt; // 좋아요 개수
    private boolean likeStatus; // 좋아요 한 여부
    private List<DetailReviewResponse> reviews; // 강의 리뷰

    // hashtags, reviews, likeStatus 따로 받아와야 함
    public static DetailLectureResponse from(Lecture lecture){
        return DetailLectureResponse.builder()
                .lectureId(lecture.getLectureId())
                .lectureTitle(lecture.getLectureTitle())
                .lecturer(lecture.getLecturer())
                .siteName(lecture.getSiteName())
                .lectureUrl(lecture.getLectureUrl())
                .thumbnailUrl(lecture.getThumbnailUrl())
                .build();
    }

}
