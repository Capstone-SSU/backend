package com.example.demo.lecture.dto;

import com.example.demo.lecture.Lecture;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class LikedLecturesForRecommendResponse {
    private long lectureId;
    private List<String> hashtags; // 해시태그 리스트

    public static LikedLecturesForRecommendResponse from(Lecture lecture){
        return LikedLecturesForRecommendResponse.builder()
                .lectureId(lecture.getLectureId())
                .build();
    }
}
