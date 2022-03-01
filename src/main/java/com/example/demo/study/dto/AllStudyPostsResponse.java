package com.example.demo.study.dto;

import com.example.demo.user.dto.SimpleUserDto;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AllStudyPostsResponse {

    private long studyPostId;

    private String studyCategoryName;  //우리가 만들어둔 피그마에는 없는데 표시해주는게 낫나 싶어서 일단은 추가..

    private String studyTitle;

    private String studyRecruitState; //string 으로

    private String studyLocation;

    private LocalDateTime studyCreatedDate;

    private Integer studyLikeCount;

    private SimpleUserDto studyPostWriter;
}
