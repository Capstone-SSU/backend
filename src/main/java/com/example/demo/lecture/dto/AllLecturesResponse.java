package com.example.demo.lecture.dto;

import com.example.demo.lecture.Lecture;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AllLecturesResponse {
    private long lectureId;
    private String lectureTitle;
    private String thumbnailUrl;
    private int likeCnt; // 좋아요 개수
    private double avgRate; // 별점

    public static AllLecturesResponse from(Lecture lecture){
        return AllLecturesResponse.builder()
                .lectureId(lecture.getLectureId())
                .lectureTitle(lecture.getLectureTitle())
                .thumbnailUrl(lecture.getThumbnailUrl())
                .avgRate(lecture.getAvgRate())
                .build();
    }
}
