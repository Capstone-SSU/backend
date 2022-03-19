package com.example.demo.mypage.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class LikedRoadmapsResponse {
    private long roadmapId;
    private String roadmapTitle;
    private String roadmapWriterCompany; //뱃지 표시용
    private List<String> lectureThumbnails;
    private LocalDateTime roadmapCreatedDate; // 작성일자
    private String roadmapWriterNickname; // 닉네임
}
