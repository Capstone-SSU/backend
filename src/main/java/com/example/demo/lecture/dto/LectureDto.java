package com.example.demo.lecture.dto;

import com.example.demo.lecture.Lecture;
import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

// 강의 등록 시 사용하는 DTO
@Getter
@Setter
@NoArgsConstructor
@ApiModel(value = "회원 정보", description = "아이디, 이름, 비밀번호, 이메일, 주소, 가입날짜를 가진 Domain Class")
public class LectureDto {
    private String lectureUrl;
    private String lectureTitle;
    private String lecturer;
    private String siteName;
    private String thumbnailUrl;
    private List<String> hashtags;
}
