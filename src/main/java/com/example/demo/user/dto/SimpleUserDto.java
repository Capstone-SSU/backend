package com.example.demo.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleUserDto {
    private Long userId;

    private String userNickname;

//    private String userCompany;

    private String userProfileImg;
}
