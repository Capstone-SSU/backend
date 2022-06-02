package com.example.demo.mypage.dto;

import com.example.demo.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class MyPageResponse {
    private String userNickname;
    private String userProfileImg;
    private String githubUrlName;
    private String userCompany;
    private List<LikedLecturesResponse> likedLectures;
    private List<LikedStudiesResponse> likedStudies;
    private List<LikedRoadmapsResponse> likedRoadmaps;
    private List<MyReviewsResponse> myReviews;
    private List<MyStudiesResponse> myStudies;
    private List<MyRoadmapsResponse> myRoadmaps;

    public static MyPageResponse from(User user){
        return MyPageResponse.builder()
                .userNickname(user.getUserNickname())
                .userProfileImg(user.getUserProfileImg())
                .githubUrlName(user.getGithubUrlName())
                .userCompany(user.getUserCompany())
                .build();
    }
}
