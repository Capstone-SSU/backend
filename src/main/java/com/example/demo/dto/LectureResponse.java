package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class LectureResponse {
    LectureOnlyDto lectureOnlyDto; // 강의 내용만 담은거
    private double avgRate; // 별점 평균
//    List<HashtagResponse> hashtags; // 해시태그 가장 많은 3개 리스트
//    //    List<Like>
//    List<ReviewOnlyDto> reviewOnlyDto;
}
