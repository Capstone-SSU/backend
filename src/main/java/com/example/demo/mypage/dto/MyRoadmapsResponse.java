package com.example.demo.mypage.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class MyRoadmapsResponse {
    private long roadmapId;
    private String roadmapTitle;
    private String roadmapWriterCompany; //뱃지 표시용
    private List<String> lectureThumbnails;
}
