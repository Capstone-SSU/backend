package com.example.demo.roadmap.repository;

import com.example.demo.lecture.Lecture;
import com.example.demo.roadmap.RoadMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoadMapRepository extends JpaRepository<RoadMap,Long>,CustomRoadMapRepository {
    Optional<RoadMap> findByRoadmapGroupIdAndLecture(Integer groupId, Lecture lecture);
}
