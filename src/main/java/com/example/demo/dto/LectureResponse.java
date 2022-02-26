package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class LectureResponse {
    private long lectureId;
    private String lectureTitle;
    private String lecturer;
    private String siteName;
    private String lectureUrl;
    private String thumbnailUrl;
    private double avgRate; // 별점 평균
    private List<String> hashtags; // 해시태그 가장 많은 3개 리스트
    private int reviewCnt; // 리뷰 개수
    // 좋아요 개수
    private List<ReviewOnlyDto> reviews; // 강의 리뷰
}
