package com.example.demo.review;

import com.example.demo.lecture.Lecture;
import com.example.demo.report.Report;
import com.example.demo.review.dto.ReviewPostDto;
import com.example.demo.user.domain.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="reviews")
@Data
@NoArgsConstructor
@JsonIgnoreProperties(value={"user", "lecture","reports","lectureHashtags"})
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    @NotNull
    private long reviewId;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = User.class)
    @JoinColumn(name="user_id")
    @JsonBackReference // 연관관계의 주인
    @NotNull
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Lecture.class)
    @JoinColumn(name="lecture_id")
    @JsonBackReference // 연관관계의 주인
    @NotNull
    private Lecture lecture;

    @Column
    @NotNull
    private int rate;

    @Column(length = 45)
    @NotNull
    private String commentTitle;

    @Lob
    @Column
    @NotNull
    private String comment;

    @Column
    @NotNull
    private LocalDateTime createdDate;

    @Column
    @NotNull
    private int reportCount;

    @Column
    @NotNull
    private int reviewStatus=1;

    @OneToMany(mappedBy = "review", targetEntity = Report.class) // 하나의 리뷰글에 여러개의 신고, Report 엔티티의 review 라는 컬럼과 연결되어 있음
    @JsonManagedReference
    private List<Report> reports=new ArrayList<>();

    // rate, commentTitle, comment
    @Builder
    public Review(int rate, String commentTitle, String comment) {
        this.rate = rate;
        this.createdDate = LocalDateTime.now();
        this.commentTitle = commentTitle;
        this.comment = comment;
    }

    public void setLectureReview(ReviewPostDto reviewPostDto, User user, Lecture lecture) {
        this.rate = reviewPostDto.getRate();
        this.createdDate = LocalDateTime.now();
        this.commentTitle = reviewPostDto.getCommentTitle();
        this.comment = reviewPostDto.getComment();
        this.lecture = lecture;
        this.user = user;
    }

    public void updateReviewReportCount(int count){
        this.reportCount=count;
    }

    public void updateReviewStatus(){
        this.reviewStatus = 0;
    }

}
