package com.example.demo.study.dto;

import com.example.demo.user.dto.DetailUserDto;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AllStudyPostsResponse {

    private long studyPostId;

    private String studyCategoryName;

    private String studyTitle;

    private String studyContent;

    private Integer studyRecruitStatus; //string 으로

    private String studyLocation;

    private Integer studyMinReq;

    private Integer studyMaxReq; // null 이면 최대인원 없음

    private LocalDateTime studyCreatedDate;

    private Integer studyReportCount;

    private DetailUserDto user;
}
