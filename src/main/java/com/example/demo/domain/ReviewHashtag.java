package com.example.demo.domain;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Table(name="review_hashtags")
@NoArgsConstructor
@Getter
@Setter
public class ReviewHashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long reviewTagId;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = Review.class)
    @JoinColumn(name="reviewId")
    @JsonBackReference // 연관관계의 주인
    @NotNull
    private Review review;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = Hashtag.class)
    @JoinColumn(name="hashtagId")
    @JsonBackReference // 연관관계의 주인
    @NotNull
    private Hashtag hashtag;
}
