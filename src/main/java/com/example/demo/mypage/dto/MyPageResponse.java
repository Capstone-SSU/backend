package com.example.demo.mypage.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MyPageResponse {
    private String userNickname;
    private String userProfileImg;
    private String githubUrlName;
    private String userCompany;
    private List<MyReviewsResponse> myReviews;
    private List<MyStudiesResponse> myStudies;
    private List<MyRoadmapsResponse> myRoadmaps;
}
