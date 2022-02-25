package com.example.demo.domain;


import com.example.demo.dto.StudyPostDTO;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name="studyPosts")
public class StudyPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long studyPostId;

    @Column
    @NotNull
    private String studyCategoryName; // 지정된 카테고리만 선택, 드롭다운은 프론트에서 제공

    @Column
    @NotNull
    private String studyTitle;

    @Column
    @NotNull
    private String studyContent;

    @Column(columnDefinition = "integer default 1") // 기본 1, 모집중
    @NotNull
    private Integer studyRecruitStatus=1; // 모집중 or 모집완료

    @Column
    @NotNull
    private String studyLocation;

    @Column
    @NotNull
    private Integer studyMinReq;

    @Column
    private Integer studyMaxReq;

    @Column
    @NotNull
    @CreatedDate
    private LocalDateTime studyCreatedDate= LocalDateTime.now();

    @Column(columnDefinition = "integer default 1")
    @NotNull
    private Integer studyStatus=1; // 해당 스터디글이 삭제되었는지 등을 바로 데이터베이스에서 지우는 것이 아닌, 해당 컬럼의 값 변경으로 우선 표시 (0이면 삭제된글)

    @Column(columnDefinition = "integer default 0")
    @NotNull
    private Integer studyReportCount=0;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = User.class) // M:1 관계일 때, M 에 해당하는 테이블에 해당 annotation 이 붙는다. (한 명의 유저에게 M개의 스터디글)
    @JoinColumn(name="userId") // join이 이루어지는 기준, 즉 외래키에 대한 설정 name: 매핑할 테이블 이름_그 테이블의 연결할 컬럼 이름
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "studyPost", targetEntity = Report.class) // 하나의 스터디글에 여러개의 신고, Report 엔티티의 studyPost 라는 컬럼과 연결되어 있음
    @JsonManagedReference
    private List<Report> reports=new ArrayList<>();

    @OneToMany(mappedBy = "studyPost",targetEntity = Like.class)
    @JsonManagedReference
    private List<Like> likes =new ArrayList<>();

    @OneToMany(mappedBy = "studyPost",targetEntity = StudyComment.class)
    @JsonManagedReference
    private List<StudyComment> studyComments =new ArrayList<>();


    @Builder
    public StudyPost(StudyPostDTO dto){
        setStudyPost(dto);
    }

    public void updateRecruitStatus(int status){
        this.studyRecruitStatus=status;
    }

    public void updateStudyReportCount(int count){ this.studyReportCount=count; }

    public void updateStudyStatus(int status){this.studyStatus=status;}

    public void setStudyPost(StudyPostDTO dto){
        this.studyTitle=dto.getStudyTitle();
        this.studyContent=dto.getStudyContent();
        this.studyCategoryName=dto.getStudyCategoryName();
        this.studyLocation=dto.getStudyLocation();
        this.studyMinReq=dto.getStudyMinReq();
        this.studyMaxReq=dto.getStudyMaxReq();
    }









}
