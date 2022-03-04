package com.example.demo.roadmap.dto;

import com.example.demo.user.dto.SimpleUserDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DetailRoadmapResponse {
    private Integer roadmapGroupId;
    private String roadmapTitle;
    private String roadmapRecommendation;
    private Integer likeCount;
    private SimpleUserDto roadmapWriter;
    private Boolean isLikedByUser;
    private Boolean isThisUserRoadmapWriter;
    private List<DetailRoadmapLectureResponse> lectures;
}
