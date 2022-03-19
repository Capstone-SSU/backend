package com.example.demo.mypage.dto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class LikedStudiesResponse {
    private long studyPostId;
    private String studyTitle;
    private LocalDateTime studyCreatedDate;
    private String studyLocation;
    private String studyRecruitState;
    private String studyCategoryName;
    private String nickname;
    private String profileImage;

    @Builder
    public LikedStudiesResponse(long studyPostId, String studyTitle, LocalDateTime studyCreatedDate, String studyLocation, String studyRecruitState, String studyCategoryName, String nickname, String profileImage) {
        this.studyPostId = studyPostId;
        this.studyTitle = studyTitle;
        this.studyCreatedDate = studyCreatedDate;
        this.studyLocation = studyLocation;
        this.studyRecruitState = studyRecruitState;
        this.studyCategoryName = studyCategoryName;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }
}