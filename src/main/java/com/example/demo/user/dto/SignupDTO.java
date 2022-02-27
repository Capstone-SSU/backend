package com.example.demo.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupDTO {
    private String email;
    private String nickname;
    private String name;
    private String password;
    private String imageUrl; // imgUrl은 null로 오거나 (이미지가 없는 경우), url이 오거나 (이미지를 등록한 경우)
}
