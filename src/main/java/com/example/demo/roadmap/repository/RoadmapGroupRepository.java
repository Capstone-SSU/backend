package com.example.demo.roadmap.repository;

import com.example.demo.roadmap.RoadMapGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoadmapGroupRepository extends JpaRepository<RoadMapGroup, Long>,CustomRoadmapGroupRepository {
}
