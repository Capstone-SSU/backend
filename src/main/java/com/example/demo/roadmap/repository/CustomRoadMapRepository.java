package com.example.demo.roadmap.repository;

import com.example.demo.roadmap.RoadMap;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomRoadMapRepository {
    Integer findMaxGroupId();
    List<RoadMap> findAllRoadmapsByGroupId(Integer groupId);
}
