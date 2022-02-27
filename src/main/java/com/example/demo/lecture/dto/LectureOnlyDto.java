package com.example.demo.lecture.dto;
import lombok.Data;

@Data
public class LectureOnlyDto {
    private long lectureId;
    private String lectureTitle;
    private String lecturer;
    private String siteName;
    private String lectureUrl;
    private String thumbnailUrl;
}
