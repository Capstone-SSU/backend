package com.example.demo.lecture.dto;

import com.example.demo.lecture.Lecture;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AllLecturesResponse {
    private long lectureId;
    private String lectureTitle;
    private String thumbnailUrl;
    private int likeCnt; // 좋아요 개수
    private double avgRate; // 별점

    @Builder
    public AllLecturesResponse(long lectureId, String lectureTitle, String thumbnailUrl, int likeCnt, double avgRate) {
        this.lectureId = lectureId;
        this.lectureTitle = lectureTitle;
        this.thumbnailUrl = thumbnailUrl;
        this.likeCnt = likeCnt;
        this.avgRate = avgRate;
    }

    public static AllLecturesResponse from(Lecture lecture){
        return AllLecturesResponse.builder()
                .lectureId(lecture.getLectureId())
                .lectureTitle(lecture.getLectureTitle())
                .thumbnailUrl(lecture.getThumbnailUrl())
                .likeCnt(lecture.getLikes().size())
                .avgRate(lecture.getAvgRate())
                .build();
    }
}
