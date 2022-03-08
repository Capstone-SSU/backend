package com.example.demo.roadmap.repository;

import com.example.demo.roadmap.RoadMap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoadmapRepository extends JpaRepository<RoadMap,Long> {
}
