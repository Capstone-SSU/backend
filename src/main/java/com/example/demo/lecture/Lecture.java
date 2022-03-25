package com.example.demo.lecture;

import com.example.demo.like.Like;
import com.example.demo.user.User;
import com.example.demo.review.Review;
import com.fasterxml.jackson.annotation.*;
import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="lectures")
@Data
@NoArgsConstructor
@JsonIgnoreProperties(value={"likes", "reviews"})
public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long lectureId;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = User.class)
    @JoinColumn(name="userId")
    @JsonBackReference
    private User user;

    @Column
    @NotNull
    private String lectureTitle;

    @Column
    @NotNull
    private String lecturer;

    @Column
    @NotNull
    private String siteName;

    @Column
    @NotNull
    private String lectureUrl;

    @Column
    @NotNull
    private String thumbnailUrl;

    @Column
    @NotNull
    private Double avgRate=0.0;

    // lecture : review = 1:N
    @OneToMany(mappedBy = "lecture", targetEntity = Review.class)
    @JsonManagedReference
    private List<Review> reviews =new ArrayList<>();

    // lecture : like = 1:N
    @OneToMany( mappedBy = "lecture", targetEntity = Like.class)
    @JsonManagedReference
    private List<Like> likes =new ArrayList<>();

    @Builder
    public Lecture(String lectureTitle, String lecturer, String siteName, String lectureUrl, String thumbnailUrl) {
        this.lectureTitle = lectureTitle;
        this.lecturer = lecturer;
        this.siteName = siteName;
        this.lectureUrl = lectureUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

//    public static Lecture from(LectureDto lectureDto){
//        return Lecture.builder()
//                .lectureId()
//                .lectureTitle(lectureDto.getLectureTitle())
//                .lecturer(lectureDto.getLecturer())
//                .lectureUrl(lectureDto.getLectureUrl())
//                .siteName(lectureDto.getSiteName())
//                .avgRate(0.0)
//                .reviews(null)
//                .likes(null)
//                .thumbnailUrl(lectureDto.getThumbnailUrl())
//                .build();
//    }
}
