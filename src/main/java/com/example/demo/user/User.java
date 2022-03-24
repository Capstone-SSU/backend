package com.example.demo.user;

import com.example.demo.like.Like;
import com.example.demo.lecture.Lecture;
import com.example.demo.report.Report;
import com.example.demo.review.Review;
import com.example.demo.roadmap.RoadMapGroup;
import com.example.demo.study.domain.StudyComment;
import com.example.demo.study.domain.StudyPost;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="users")
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long userId;

    @Column(unique = true)
    @NotNull
    private String userNickname;

    @Column(unique = true)
    @NotNull
    private String userEmail;

    @Column
    @NotNull
    private String userPassword;

    @Column
    @NotNull
    private String userName;

    @Column
    private String userCompany;

    @Column
    private String userProfileImg;

    @Column
    @NotNull
    private String role;

    @Column
    private String loginProvider;

    @Column(unique=true)
    private String githubUrlName;

    @Column
    private Boolean reviewWriteStatus=false;

    @Column
    private Integer readCount=0;

    @Column
    private Boolean publicProfileStatus=false;

    @OneToMany(mappedBy="user", targetEntity = Lecture.class)
    @JsonManagedReference
    private List<Lecture> lectures = new ArrayList<>();

    @OneToMany(mappedBy="user", targetEntity = Review.class)
    @JsonManagedReference
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user", targetEntity = Report.class)
    private List<Report> reports = new ArrayList<>();

    @OneToMany(mappedBy = "user", targetEntity = StudyPost.class) // StudyPost와의 양방향 매핑을 위해 추가, 연관관계의 주인은 studyPost entity
    @JsonManagedReference
    private List<StudyPost> studyPosts=new ArrayList<>();

    //한 명의 user가 여러개의 관심글 -> 1이 User
    @OneToMany(mappedBy = "user", targetEntity = Like.class)
    @JsonManagedReference
    private List<Like> likes =new ArrayList<>();

    @OneToMany(mappedBy = "user", targetEntity = StudyComment.class)
    @JsonManagedReference
    private List<StudyComment> studyComments =new ArrayList<>();

    @OneToMany(mappedBy = "user", targetEntity = RoadMapGroup.class)
    @JsonManagedReference
    private List<RoadMapGroup> roadmapGroups =new ArrayList<>();

    @Builder
    public User(String name, String nickname, String email, String pwd){
        this.userName=name;
        this.userNickname=nickname;
        this.userEmail=email;
        this.userPassword=pwd;
        this.role="USER";
        // 이 외의 값은 초기 builder 패턴으로 생성 시에 NULL로 들어간다.
    }

    //디폴트: USER, 관리자라면: ADMIN으로 update
    public void updateUserRole(){
        this.role="ADMIN";
    }

    public void updateProfileImage(String url){
        this.userProfileImg=url;
    }

    public void updateReviewStatus(){
        if(this.reviewWriteStatus == true)
            this.reviewWriteStatus = false;
        else
            this.reviewWriteStatus = true;
    }

    public void updateReadCount(){
        this.readCount += 1;
    }

    public void updateProfileStatus(){
        if(this.publicProfileStatus == true) // 공개라면
            this.publicProfileStatus = false;
        else
            this.publicProfileStatus = true;
    }

    public void updateUserCompany(String company){
        this.userCompany=company;
    }

    public void updatePassword(String password){
        this.userPassword = password;
    }

    public void updateProfile(String nickname, String url, String githubUrlName){
        this.userNickname = nickname;
        this.userProfileImg = url;
        this.githubUrlName = githubUrlName;
    }

    public void setGithubProvider(String provider){
        this.loginProvider=provider;
    }

    public void updateGithubUrlName(String userName){
        this.githubUrlName=userName;
    }

    // 소속인증 (userCompany) 은 처음 User 데이터 생성 시에 들어가는 걊이 아니므로 생략

}
