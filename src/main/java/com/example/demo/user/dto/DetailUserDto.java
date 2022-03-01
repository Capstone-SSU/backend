package com.example.demo.user.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DetailUserDto {
    //User 객체에서 외래키 리스트들을 제외한 모든 정보

    private Long userId;

    private String userNickname;

    private String userEmail;

    private String userPassword;

    private String userName;

    private String userCompany;

    private String userProfileImg;

    private String role;

    private String loginProvider;

    private String githubUrlName;
}
