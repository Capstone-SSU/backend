package com.example.demo.study.domain;

import com.example.demo.report.Report;
import com.example.demo.user.User;
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
@Table(name = "studyComments")
@Data
@NoArgsConstructor
public class StudyComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studyCommentId;

    @Column
    @NotNull
    private String commentContent;

    @Column
    @NotNull
    private Integer commentStatus=1; // 1이면 존재, 0이면 삭제됨

    @Column
    @NotNull
    @CreatedDate
    private LocalDateTime commentCreatedDate= LocalDateTime.now();

    @Column
    @NotNull
    private Integer commentClass; // 0이면 부모댓글 (원댓글), 1이면 자식댓글 (대댓글)

    @Column
    @NotNull
    private Long commentGroupId; // 몇번 댓글에 대한 모음인지 카운트 (부모댓글의 studyCommentId == 대댓글들의 groupId)
    //프론트에서 댓글 달린거 넘겨줄 때
    // 1) 원댓글(부모댓글)이면: "parent":0  으로 넘겨주고 -> 0으로 넘어오면 새롭게 등록된 애가 부모 댓글임을 확인 => 이 댓글의 pk를 groupId로 저장
    // 2) 대댓글이면: "parent":{얘의 부모댓글의 id} 로 넘겨줘야함 -> 넘어온 부모 댓글 id를 이 댓글의 groupId로 저장

    @Column(columnDefinition = "integer default 0")
    @NotNull
    private Integer commentReportCount=0; // 신고횟수

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
    @JoinColumn(name="userId")
    @JsonBackReference
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = StudyPost.class) // 하나의 스터디글에 여러개의 댓글
    @JoinColumn(name="studyPostId")
    @JsonBackReference
    private StudyPost studyPost;

    @OneToMany(mappedBy = "studyComment", targetEntity = Report.class) // 하나의 스터디글에 여러개의 신고, Report 엔티티의 studyPost 라는 컬럼과 연결되어 있음
    @JsonManagedReference
    private List<Report> reports=new ArrayList<>();

    @Builder
    public StudyComment(String content, Integer classId){
        this.commentClass=classId;
        this.commentContent=content;
    }

    public void updateGroupId(Long groupId){
        this.commentGroupId=groupId;
    }

    public void updateCommentContent(String content){
        this.commentContent=content;
    }

    public void updateStudyCommentReportCount(Integer count){
        this.commentReportCount=count;
    }

    public void updateStudyCommentStatus(int status){this.commentStatus=status;}


}
