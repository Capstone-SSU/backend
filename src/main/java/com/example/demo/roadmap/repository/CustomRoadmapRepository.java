package com.example.demo.roadmap.repository;

import com.example.demo.lecture.Lecture;
import com.example.demo.roadmap.RoadMap;
import com.example.demo.roadmap.RoadMapGroup;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomRoadmapRepository {
    List<RoadMap> findAllRoadmapsByGroup(RoadMapGroup group);
    void updateExistingRoadmaps(List<Lecture> lectures, RoadMapGroup group);
    //한 로드맵 그룹에 대해, 새롭게 수정된 강의 목록 -> 이미 디비에 저장된 강의 목록들을 찾아옴
    List<RoadMap> findRoadmapsByGroupAndLectures(RoadMapGroup group, List<Lecture> lectures);
}
