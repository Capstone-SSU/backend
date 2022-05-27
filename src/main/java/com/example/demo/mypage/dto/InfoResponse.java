package com.example.demo.mypage.dto;

import com.example.demo.lecture.Lecture;
import com.example.demo.review.Review;
import com.example.demo.review.dto.DetailReviewResponse;
import com.example.demo.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InfoResponse {
    private String userEmail;
    private String userNickname;
    private String userProfileImg;
    private String githubUrlName;
    private Boolean publicProfileStatus;

    @Builder
    public InfoResponse(String userEmail, String userNickname, String userProfileImg, String githubUrlName, Boolean publicProfileStatus) {
        this.userEmail = userEmail;
        this.userNickname = userNickname;
        this.userProfileImg = userProfileImg;
        this.githubUrlName = githubUrlName;
        this.publicProfileStatus = publicProfileStatus;
    }

    public static InfoResponse from(User user){
        return InfoResponse.builder()
                .userEmail(user.getUserEmail())
                .userNickname(user.getUserNickname())
                .userProfileImg(user.getUserProfileImg())
                .publicProfileStatus(user.getPublicProfileStatus())
                .build();
    }
}
