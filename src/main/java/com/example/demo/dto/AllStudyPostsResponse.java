package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AllStudyPostsResponse {

    private long studyPostId;

    private String studyCategoryName;

    private String studyTitle;

    private String studyContent;

    private Integer studyRecruitStatus; //1이면 모집중, 0이면 모집완료

    private String studyLocation;

    private Integer studyMinReq;

    private Integer studyMaxReq; // null 이면 최대인원 없음

    private LocalDateTime studyCreatedDate;

    private Integer studyReportCount;

    private UserOnlyDto user;
}
