package com.example.demo.roadmap.repository;

import com.example.demo.roadmap.RoadMap;
import com.example.demo.roadmap.RoadMapGroup;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomRoadmapRepository {
    List<RoadMap> findAllRoadmapsByGroup(RoadMapGroup group);
}
