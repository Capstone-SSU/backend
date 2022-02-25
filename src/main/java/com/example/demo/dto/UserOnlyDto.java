package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserOnlyDto {
    //User 엔티티에 들어있는 외래키 리스트들이 필요없지만, 같이 반환되어서 지저분해지는 상황을 없애기 위해,,,,
    //가장 기본적인 User 정보들만 들어가는 DTO를 만들었습니다

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
