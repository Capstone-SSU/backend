package com.example.demo.domain;


import antlr.CommonAST;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name="studyPost")
public class StudyPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long studyPostId;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = User.class) // M:1 관계일 때, M 에 해당하는 테이블에 해당 annotation 이 붙는다. (한 명의 유저에게 M개의 스터디글)
    @JoinColumn(name="user_userId") // join이 이루어지는 기준, 즉 외래키에 대한 설정 name: 매핑할 테이블 이름_그 테이블의 연결할 컬럼 이름
    @JsonManagedReference
    private User user;

    @Column
    @NotNull
    private String studyCategoryName; // 지정된 카테고리만 선택, 드롭다운은 프론트에서 제공

    @Column
    @NotNull
    private String studyTitle;

    @Column
    @NotNull
    private String studyContent;

    @Column
    @NotNull
    private int studyRecruitStatus; // 모집중 or 모집완료

    @Column
    @NotNull
    private String studyLocation;

    @Column
    @NotNull
    private int studyMinReq;

    @Column
    private int studyMaxReq;

    @Column
    @NotNull
    @CreatedDate
    private LocalDateTime studyCreatedDate= LocalDateTime.now();

    @Column(columnDefinition = "integer default 1")
    @NotNull
    private int studyStatus; // 해당 스터디글이 삭제되었는지 등을 바로 데이터베이스에서 지우는 것이 아닌, 해당 컬럼의 값 변경으로 우선 표시

    @Column(columnDefinition = "integer default 0")
    @NotNull
    private int studyReportCount;


    @Builder
    public StudyPost(String title, String content, String category, String location, int recruit, int min){
        this.studyTitle=title;
        this.studyContent=content;
        this.studyCategoryName=category;
        this.studyLocation=location;
        this.studyRecruitStatus=recruit;
        this.studyMinReq=min;
        // 이 외의 값은 초기 builder 패턴으로 생성 시에 NULL로 들어간다.
    }

    public void updateRecruitStatus(int status){
        this.studyRecruitStatus=status;
    }









}
