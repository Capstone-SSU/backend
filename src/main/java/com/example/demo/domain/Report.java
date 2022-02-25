package com.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "reports")
@NoArgsConstructor
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @Column
    private String reportContent; // 신고사유 (사용자 선택 + 직접입력)

    @Column
    private Integer reportDivision; // 0이면 스터디글, 1이면 리뷰글

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = StudyPost.class) // M:1 관계일 때, M 에 해당하는 테이블에 해당 annotation 이 붙는다. (한 명의 유저에게 M개의 스터디글)
    @JoinColumn(name="studyPostId") // join이 이루어지는 기준, 즉 외래키에 대한 설정 name: 매핑할 테이블 이름_그 테이블의 연결할 컬럼 이름
    @JsonBackReference
    private StudyPost studyPost;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = Review.class)
    @JoinColumn(name = "reviews_reviewId")
    @JsonBackReference
    private Review review;

    @Builder
    public Report(String content, Integer division){
        this.reportContent=content;
        this.reportDivision=division;
    }

}
