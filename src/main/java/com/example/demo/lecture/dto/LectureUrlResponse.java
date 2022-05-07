package com.example.demo.lecture.dto;

import com.example.demo.lecture.Lecture;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class LectureUrlResponse {
    private String lectureUrl;
    private String lectureTitle;
    private String lecturer;
    private String siteName;
    private List<String> hashtags; // 해시태그

    public static LectureUrlResponse from(Lecture lecture){
        return LectureUrlResponse.builder()
                .lectureUrl(lecture.getLectureUrl())
                .lectureTitle(lecture.getLectureTitle())
                .lecturer(lecture.getLecturer())
                .siteName(lecture.getSiteName())
                .build();
    }
}
