package com.example.demo.roadmap.service;

import com.example.demo.roadmap.RoadMap;
import com.example.demo.roadmap.repository.RoadmapRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class RoadmapService {
    private final RoadmapRepository roadmapRepository;

    public void saveRoadmap(RoadMap roadMap){
        roadmapRepository.save(roadMap);
    }


}
