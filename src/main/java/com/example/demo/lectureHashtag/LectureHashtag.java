package com.example.demo.lectureHashtag;
import com.example.demo.hashtag.Hashtag;
import com.example.demo.lecture.Lecture;
import com.example.demo.review.Review;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Table(name="lecture_hashtags")
@NoArgsConstructor
@Getter
@Setter
public class LectureHashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long lectureTagId;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = Lecture.class)
    @JoinColumn(name="lecture_id")
    @JsonBackReference // 연관관계의 주인
    @NotNull
    private Lecture lecture;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = Hashtag.class)
    @JoinColumn(name="hashtag_id")
    @JsonBackReference // 연관관계의 주인
    @NotNull
    private Hashtag hashtag;
}
