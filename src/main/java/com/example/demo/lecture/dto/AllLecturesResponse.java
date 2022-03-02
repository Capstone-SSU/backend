package com.example.demo.lecture.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AllLecturesResponse {
    private long lectureId;
    private String lectureTitle;
    private String thumbnailUrl;
    private int likeCnt; // 좋아요 개수
    private double avgRate; // 별점
}
