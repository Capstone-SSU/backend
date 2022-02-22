package com.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;


import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="reviews")
@Data
@DynamicInsert
//@DynamicUpdate // insert, update 시 null인 field는 제외
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    @NotNull
    private long reviewId;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = User.class)
    @JoinColumn(name="userId")
    @JsonBackReference // 연관관계의 주인
    @NotNull
    private User user;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = Lecture.class)
    @JoinColumn(name="lectureId")
    @JsonBackReference // 연관관계의 주인
    @NotNull
    private Lecture lecture;

    @Column
    @NotNull
    private int rate;

    @Column
    @NotNull
    private String commentTitle;

    @Column
    @NotNull
    private String comment;

    @Column
    @NotNull
    private LocalDateTime createdDate;

    @Column
    @NotNull
    private int reportCount;

    @Column(columnDefinition = "integer default 1") // 값 할당안하면 이렇게 선언해도 null 할당됨
    @NotNull
    private int reviewStatus=1;

    // rate, commentTitle, comment
    @Builder
    public Review(int rate, LocalDateTime createdDate, String commentTitle, String comment) {
        this.rate = rate;
        this.createdDate = LocalDateTime.now();
        this.commentTitle = commentTitle;
        this.comment = comment;
    }
}
