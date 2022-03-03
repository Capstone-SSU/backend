package com.example.demo.roadmap.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoadmapUploadLectureDto {
    private Long lectureId;
    private String lectureTitle;
    private String thumbnailUrl;
    private List<String> hashTags;
}
