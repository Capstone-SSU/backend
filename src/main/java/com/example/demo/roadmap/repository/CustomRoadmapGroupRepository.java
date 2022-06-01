package com.example.demo.roadmap.repository;

import com.example.demo.roadmap.RoadMapGroup;
import com.example.demo.user.domain.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomRoadmapGroupRepository {
    List<RoadMapGroup> findAllRoadmapsWithFilter(String[] keywords);
    List<RoadMapGroup> findAllRoadmapsByUser(User user);
}
