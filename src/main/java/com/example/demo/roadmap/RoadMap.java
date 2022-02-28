package com.example.demo.roadmap;

import com.example.demo.lecture.Lecture;
import com.example.demo.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "roadmap")
public class RoadMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roadmapId;

    @Column(nullable = false)
    private String roadmapTitle;

    @Column
    private String roadmapRecommendation; //추천대상

    @Column
    private Integer roadmapStatus=1;  //0이면 삭제된 것

    @Column(nullable = false)
    private Integer roadmapGroupId; //해당 강의가 어떤 로드맵에 속해있는지

    @Column
    @CreatedDate
    private LocalDateTime roadmapCreatedDate=LocalDateTime.now();

    @Column(nullable = false)
    private Integer roadmapLectureOrder; //한 로드맵 에서 이 강의의 순서 지정 -> 오름차순으로 정렬

    //근데 그냥 하나의 roadmap 클래스 안에,,, 렉쳐 컬럼 따로 가지면 될 것 같아서 단방향 OneToOne으로 가겠습니다
    @OneToOne(targetEntity = Lecture.class)
    @JoinColumn(name = "lectureId")
    private Lecture lecture;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "userId")
    @JsonBackReference
    private User user; //직성자 객체 저장

    @Builder
    public RoadMap(String title, String recommend, Lecture lecture, Integer order, Integer groupId, User user){
        this.roadmapTitle=title;
        this.roadmapRecommendation=recommend;
        this.lecture=lecture;
        this.roadmapLectureOrder=order;
        this.roadmapGroupId=groupId;
        this.user=user;
    }

}
