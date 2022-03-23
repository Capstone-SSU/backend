package com.example.demo.mypage.dto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class MyInfoEditDto {
    private MultipartFile userProfileImg;
    private String userNickname;
    private String githubUrlName;
    private String password;
    private String newPassword;
    private String confirmPassword;
}
