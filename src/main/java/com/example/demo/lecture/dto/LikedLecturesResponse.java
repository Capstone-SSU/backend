package com.example.demo.lecture.dto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LikedLecturesResponse {
    private long lectureId;
    private String lectureTitle;
    private String lecturer;
    private String siteName;
    private String thumbnailUrl;
    private double avgRate; // 별점 평균
    private List<String> hashtags; // 해시태그 가장 많은 3개 리스트
}