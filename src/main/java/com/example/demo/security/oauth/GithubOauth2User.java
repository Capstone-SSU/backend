package com.example.demo.security.oauth;

import lombok.Data;


//github 소셜 로그인으로 받아온 정보만 저장해둘 객체 -> 나중에 이 객체 값들을 기반으로 회원가입 창에 자동 표시, user에 update
@Data
public class GithubOauth2User {
    private String username;
    private String name;
    private String email;
    private String avatarUrl;
}
