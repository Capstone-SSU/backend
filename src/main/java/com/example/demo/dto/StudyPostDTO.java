package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudyPostDTO {
    private  String title;
    private  String content;
    private Long userId;
    private String category;
    private String location;
    private Integer recruitStatus; // 모집여부
    private Integer minReq;
    private Integer maxReq;
}
