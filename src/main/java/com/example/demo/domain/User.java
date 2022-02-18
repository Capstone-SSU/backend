package com.example.demo.domain;

import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="user")
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

//    @Column(unique = true)
//    private String userGithub;

    @Column
    private String userProfileImg;

    @Column
    @NotNull
    private String role;

    @Column
    private String loginProvider;

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

    public void updateUserCompany(String company){
        this.userCompany=company;
    }

    public void updateByGithubLogin(String name,String nickname,String email,String profileUrl){
        this.userName=name;
        this.userNickname=nickname;
        this.userEmail=email;
        this.userProfileImg=profileUrl;
    }

    public void setGithubProvider(String provider){
        this.loginProvider=provider;
    }

    // 소속인증 (userCompany) 은 처음 User 데이터 생성 시에 들어가는 걊이 아니므로 생략

}
