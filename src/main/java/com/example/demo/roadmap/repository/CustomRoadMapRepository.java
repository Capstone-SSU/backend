package com.example.demo.roadmap.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface CustomRoadMapRepository {
    Integer findMaxGroupId();
}
