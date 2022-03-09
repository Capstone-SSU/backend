package com.example.demo.roadmap;

import com.example.demo.lecture.Lecture;
import com.example.demo.like.Like;
import com.example.demo.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "roadmaps")
public class RoadMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roadmapId;

    @Column(nullable = false)
    private Integer roadmapLectureOrder; //한 로드맵 에서 이 강의의 순서 지정 -> 오름차순으로 정렬

    @Column(nullable = false)
    private Integer roadmapStatus=1;

    //근데 그냥 하나의 roadmap 클래스 안에,,, 렉쳐 컬럼 따로 가지면 될 것 같아서 단방향 OneToOne으로 가겠습니다
    @OneToOne(targetEntity = Lecture.class)
    @JoinColumn(name = "lectureId")
    private Lecture lecture;

    @ManyToOne(targetEntity = RoadMapGroup.class)
    @JoinColumn(name = "roadmapGroupId")
    @JsonBackReference
    private RoadMapGroup roadmapGroup; //직성자 객체 저장

    @Builder
    public RoadMap(Lecture lecture, Integer order, RoadMapGroup group){
        this.lecture=lecture;
        this.roadmapLectureOrder=order;
        this.roadmapGroup=group;
    }

    public void updateRoadmapLectureOrder(Integer order){
        this.roadmapLectureOrder=order;
    }

}
