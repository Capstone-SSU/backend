package com.example.demo.like;

import com.example.demo.lecture.Lecture;
import com.example.demo.roadmap.RoadMapGroup;
import com.example.demo.study.domain.StudyPost;
import com.example.demo.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Table(
        name = "likes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name="UniqueLectureAndUser",
                        columnNames = {"lectureId", "userId"}
                ),
                @UniqueConstraint(
                        name="UniqueStudyAndUser",
                        columnNames = {"studyPostId", "userId"}
                ),
                @UniqueConstraint(
                        name="UniqueRoadmapAndUser",
                        columnNames = {"roadmapGroupId", "userId"}
                )
        }
)
@Data
@NoArgsConstructor // jpa에는 기본 contructor가 필요함 -> 없을 경우에 "No default constructor for Entity 에러 발생" -> NoArgsConstructor로 해결 가능
@JsonIgnoreProperties(value={"user","lecture","roadmapGroup"})
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

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

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = RoadMapGroup.class)
    @JsonBackReference
    @JoinColumn(name = "roadmapGroupId")
    private RoadMapGroup roadmapGroup;

    @Builder
    public Like(User user, StudyPost post){
        this.studyPost=post;
        this.user=user;
    }

    @Builder
    public Like(Lecture lecture, User user) {
        this.lecture = lecture;
        this.user = user;
    }

    public int changeLikeStatus(int status) {
        this.likeStatus = status;
        return status;
    }

    @Builder
    public Like(RoadMapGroup roadMapGroup, User user) {
        this.roadmapGroup=roadMapGroup;
        this.user = user;
    }
}
