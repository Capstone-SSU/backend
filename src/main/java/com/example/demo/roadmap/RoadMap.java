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

    //단방향 ManyToOne (1 강의가 여러 로드맵 데이터가 될 수 있음) -> OneToOne을 쓰려면 1 강의는 1 로드맵 데이터만 되어야한다! (unique 해야함)
    @ManyToOne(targetEntity = Lecture.class)
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
