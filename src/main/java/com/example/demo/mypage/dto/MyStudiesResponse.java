package com.example.demo.mypage.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyStudiesResponse {
    private long studyPostId;
    private String studyLocation;
    private String studyCategoryName; // 지정된 카테고리만 선택, 드롭다운은 프론트에서 제공
    private String studyTitle;
    private String studyRecruitStatus; // 모집중 or 모집완료
    private String profileImage;

    public MyStudiesResponse(long studyPostId, String studyLocation, String studyCategoryName, String studyTitle, Integer studyRecruitStatus) {
        this.studyPostId = studyPostId;
        this.studyLocation = studyLocation;
        this.studyCategoryName = studyCategoryName;
        this.studyTitle = studyTitle;
        this.studyRecruitStatus = studyRecruitStatus==1?"모집중":"모집완료";
    }
}
