package com.example.demo.mypage.dto;

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

    @Builder
    public InfoResponse(String userEmail, String userNickname, String userProfileImg, String githubUrlName) {
        this.userEmail = userEmail;
        this.userNickname = userNickname;
        this.userProfileImg = userProfileImg;
        this.githubUrlName = githubUrlName;
    }
}
