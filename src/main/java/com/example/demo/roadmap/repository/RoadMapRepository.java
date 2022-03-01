package com.example.demo.roadmap.repository;

import com.example.demo.roadmap.RoadMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadMapRepository extends JpaRepository<RoadMap,Long>,CustomRoadMapRepository {
}
