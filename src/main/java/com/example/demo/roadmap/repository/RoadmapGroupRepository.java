package com.example.demo.roadmap.repository;

import com.example.demo.roadmap.RoadMapGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoadmapGroupRepository extends JpaRepository<RoadMapGroup, Long>,CustomRoadmapGroupRepository {
    Page<RoadMapGroup> findAll(Specification<RoadMapGroup> spec, Pageable pageable);
}
