package com.example.demo.report;

import com.example.demo.review.Review;
import com.example.demo.study.domain.StudyComment;
import com.example.demo.study.domain.StudyPost;
import com.example.demo.user.domain.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table(name = "reports")
@NoArgsConstructor
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @Column
//    @Convert(converter = StringToIntegerConverter.class)
    private String reportContent; // 신고사유 (사용자 선택 + 직접입력)

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class) // M:1 관계일 때, M 에 해당하는 테이블에 해당 annotation 이 붙는다. (한 명의 유저에게 M개의 스터디글)
    @JoinColumn(name="user_id") // join이 이루어지는 기준, 즉 외래키에 대한 설정 name: 매핑할 테이블 이름_그 테이블의 연결할 컬럼 이름
    @JsonBackReference
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = StudyPost.class) // M:1 관계일 때, M 에 해당하는 테이블에 해당 annotation 이 붙는다. (한 명의 유저에게 M개의 스터디글)
    @JoinColumn(name="studyPost_id") // join이 이루어지는 기준, 즉 외래키에 대한 설정 name: 매핑할 테이블 이름_그 테이블의 연결할 컬럼 이름
    @JsonBackReference
    private StudyPost studyPost;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Review.class)
    @JoinColumn(name = "review_id")
    @JsonBackReference
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = StudyComment.class)
    @JoinColumn(name = "studyComment_id")
    @JsonBackReference
    private StudyComment studyComment;

    @Builder
    public Report(String content, StudyPost post,User user){
        this.reportContent=content;
        this.studyPost=post;
        this.user=user;
    }

    @Builder
    public Report(String content, StudyComment comment,User user){
        this.reportContent=content;
        this.studyComment=comment;
        this.user=user;
    }

    @Builder
    public Report(String content, Review review){
        this.reportContent=content;
        this.review=review;
    }

}

//@Converter
//class StringToIntegerConverter implements AttributeConverter<String, Integer> {
//    @Override
//
//}