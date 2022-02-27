package com.example.demo.lecture.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LectureDto {
    private String lectureUrl;
    private String lectureTitle;
    private String lecturer;
    private String siteName;
    private String thumbnailUrl;
    private List<String> hashtags;
    private Integer rate;
    private String commentTitle;
    private String comment;
}
