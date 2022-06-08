package com.example.demo.mypage.dto;

import com.example.demo.lecture.Lecture;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikedLecturesResponse {
    private long lectureId;
    private String lectureTitle;
    private String lecturer;
    private String siteName;
    private String thumbnailUrl;
    private double avgRate; // 별점 평균
    private List<String> hashtags; // 해시태그 가장 많은 3개 리스트

    public static LikedLecturesResponse from(Lecture lecture){
        return LikedLecturesResponse.builder()
                .lectureId(lecture.getLectureId())
                .lectureTitle(lecture.getLectureTitle())
                .lecturer(lecture.getLecturer())
                .siteName(lecture.getSiteName())
                .thumbnailUrl(lecture.getThumbnailUrl())
                .avgRate(lecture.getAvgRate())
                .build();
    }
}