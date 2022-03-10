package com.example.demo.roadmap.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class AllRoadmapsResponse {
    private long roadmapId;
    private String roadmapTitle;
    private LocalDateTime roadmapCreatedDate;
    private String roadmapWriterNickname;
    private String roadmapWriterCompany; //뱃지 표시용
    private List<String> lectureThumbnails;
    private Integer roadmapLikeCount;


    @Builder
    public AllRoadmapsResponse(long roadmapId, String title, String nickname, String company, LocalDateTime date, List<String> thumbnails, Integer count){
        this.roadmapId = roadmapId;
        this.lectureThumbnails=thumbnails;
        this.roadmapCreatedDate=date;
        this.roadmapTitle=title;
        this.roadmapWriterCompany=company;
        this.roadmapLikeCount=count;
        this.roadmapWriterNickname=nickname;
    }

}
