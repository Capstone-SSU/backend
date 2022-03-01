package com.example.demo.roadmap;

import com.example.demo.roadmap.repository.RoadMapRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class RoadMapService {
    private final RoadMapRepository roadMapRepository;

    public Long saveRoadmap(RoadMap roadMap){
        RoadMap save = roadMapRepository.save(roadMap);
        return save.getRoadmapId();
    }

    public Integer getMaxRoadmapGroupId(){
        Integer maxGroupId=roadMapRepository.findMaxGroupId();
        return maxGroupId!=null?maxGroupId:0; //만약 테이블이 비어있으면 null이 return -> 그럴 경우 0을 return
    }

    public RoadMap getRoadMapById(Long roadmapId){
        Optional<RoadMap> roadMap = roadMapRepository.findById(roadmapId);
        if(roadMap.isPresent()){
            RoadMap map=roadMap.get();
            return map.getRoadmapStatus()==1?map:null;
        }else{
            return null;
        }
    }



}
