package com.example.demo.dto;

import com.example.demo.domain.Lecture;
import com.example.demo.domain.Review;
import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
public class LectureOnlyDto {
    private long lectureId;
    private String lectureTitle;
    private String lecturer;
    private String siteName;
    private String lectureUrl;
    private String thumbnailUrl;
}
