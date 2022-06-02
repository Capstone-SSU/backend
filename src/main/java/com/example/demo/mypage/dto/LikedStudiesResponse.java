package com.example.demo.mypage.dto;
import com.example.demo.study.domain.StudyPost;
import com.example.demo.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class LikedStudiesResponse {
    private long studyPostId;
    private String studyTitle;
    private LocalDateTime studyCreatedDate;
    private String studyLocation;
    private String studyRecruitState;
    private String studyCategoryName;
    private String nickname;
    private String profileImage;

//    @Builder
//    public LikedStudiesResponse(long studyPostId, String studyTitle, LocalDateTime studyCreatedDate, String studyLocation, String studyRecruitState, String studyCategoryName, String nickname, String profileImage) {
//        this.studyPostId = studyPostId;
//        this.studyTitle = studyTitle;
//        this.studyCreatedDate = studyCreatedDate;
//        this.studyLocation = studyLocation;
//        this.studyRecruitState = studyRecruitState;
//        this.studyCategoryName = studyCategoryName;
//        this.nickname = nickname;
//        this.profileImage = profileImage;
//    }
    public static LikedStudiesResponse from(StudyPost study, User user){
        return LikedStudiesResponse.builder()
                .studyPostId(study.getStudyPostId())
                .studyTitle(study.getStudyTitle())
                .studyCreatedDate(study.getStudyCreatedDate())
                .studyLocation(study.getStudyLocation())
                .studyRecruitState((study.getStudyRecruitStatus()==1) ? "모집중" : "모집완료")
                .studyCategoryName(study.getStudyCategoryName())
                .nickname(user.getUserNickname())
                .profileImage(user.getUserProfileImg())
                .build();
    }
}