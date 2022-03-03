package com.example.demo.roadmap.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DetailRoadmapLectureResponse {
    //상세 로드맵 조회 시, 그 로드맵에 등록된 강의 정보
    private Long lectureId;
    private String lectureTitle;
    private String thumbnailUrl;
    private List<String> lectureHashtags;
    private Double lectureAvgRate; //평균 별점 -> 이것만 보내면 프론트가 별에 색칠해주는건가?
    private String lectureReviewTitle;
    private String lectureReviewContent;

}
