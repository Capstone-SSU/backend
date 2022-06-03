package com.example.demo.lecture.dto;

import com.example.demo.lecture.Lecture;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AllLecturesForRecommendResponse {
    private long lectureId;
    private String lectureTitle;
    private double avgRate; // 별점
    private int reviewCnt;
    private List<String> hashtags; // 해시태그 리스트

    public static AllLecturesForRecommendResponse from(Lecture lecture){
        return AllLecturesForRecommendResponse.builder()
                .lectureId(lecture.getLectureId())
                .lectureTitle(lecture.getLectureTitle())
                .avgRate(lecture.getAvgRate())
                .reviewCnt(lecture.getReviews().size())
                .build();
    }
}
// [’강의 번호’,’강의 제목’,’평점’,’리뷰를 한 사용자의 수’,’키워드(해시)’]