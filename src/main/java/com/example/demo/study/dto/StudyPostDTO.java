package com.example.demo.study.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudyPostDTO {
    // 글 등록, 글 수정에도 동일하게 사용할 것
    private  String studyTitle;
    private  String studyContent;
    private String studyCategoryName;
    private String studyLocation;
    private Integer studyMinReq;
    private Integer studyMaxReq;
}
