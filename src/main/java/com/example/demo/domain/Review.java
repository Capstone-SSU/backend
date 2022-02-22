package com.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="reviews")
@Data
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long reviewId;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = Lecture.class)
    @JoinColumn(name="lectureId")
    @JsonBackReference // 연관관계의 주인
    private Lecture lecture;

    @Column
    @NotNull
    private int rate;

    @Column
    @NotNull
    private String comment;

    @Column
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column
    @NotNull
    private int reportCount;

    @Column(columnDefinition = "int default 1")
    @NotNull
    private int reviewStatus;

//    @Builder
//    public Review(String lectureTitle, String lecturer, String siteName, String lectureUrl, String thumbnailUrl) {
//        this.lectureTitle = lectureTitle;
//        this.lecturer = lecturer;
//        this.siteName = siteName;
//        this.lectureUrl = lectureUrl;
//        this.thumbnailUrl = thumbnailUrl;
//    }
}
