package com.example.demo.mypage.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Getter
@Setter
public class MyInfoEditDto {
//    프로필 사진
//    이메일 -> 변경불가
//    닉네임 -> 중복확인
//
//    깃허브 로 가입한 경우면 자동으로 넣어져있고,
//    아니면 본인추가 가능
//
//    현재 비밀번호
//    새 비밀번호
//    새 비밀번호 확인
    private MultipartFile userProfileImg;
    private String userNickname;
    private String githubUrlName;
}
