package com.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "likes")
@Data
@NoArgsConstructor // jpa에는 기본 contructor가 필요함 -> 없을 경우에 "No default constructor for Entity 에러 발생" -> NoArgsConstructor로 해결 가능
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @Column
    private Integer targetDivision; // 0이면 스터디글, 1이면 강의글, 2면 로드맵

    @Column(columnDefinition = "integer default 1")
    private Integer likeStatus=1; // 1이 default (최초 디비에 등록할 때는 좋아요가 눌린 상태로 저장되므로), 0은 좋아요가 취소된 것

    //하나의 글에 여러개의 관심글 -> 관심글이 M, 주인
    @ManyToOne(fetch = FetchType.EAGER,targetEntity = StudyPost.class)
    @JsonBackReference
    @JoinColumn(name = "studyPostId")
    private StudyPost studyPost;

    @ManyToOne(fetch = FetchType.EAGER,targetEntity = Lecture.class)
    @JsonBackReference
    @JoinColumn(name = "lectureId")
    private Lecture lecture;

    //한 명의 사용자가 여러개의 관심글 등록 -> 관심글이 M, 주인
    @ManyToOne(fetch = FetchType.EAGER, targetEntity = User.class)
    @JsonBackReference
    @JoinColumn(name = "userId")
    private User user;

    @Builder
    public Like(User user, Integer division){
        this.targetDivision=division;
        this.user=user;
    }
}
