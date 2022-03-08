package com.example.demo.roadmap.service;

import com.example.demo.roadmap.RoadMapGroup;
import com.example.demo.roadmap.repository.RoadmapGroupRepository;
import com.example.demo.roadmap.repository.RoadmapRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RoadmapGroupService {
    private final RoadmapGroupRepository roadmapGroupRepository;

    public RoadMapGroup saveRoadmapGroup(RoadMapGroup roadMapGroup){
        return roadmapGroupRepository.save(roadMapGroup);
    }


}
