package com.example.demo.roadmap;

import com.example.demo.dto.ResponseMessage;
import com.example.demo.lecture.Lecture;
import com.example.demo.lecture.LectureService;
import com.example.demo.roadmap.dto.RoadMapDto;
import com.example.demo.user.User;
import com.example.demo.user.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor
public class roadMapController {
    private final RoadMapService roadMapService;
    private final UserDetailsServiceImpl userDetailsService;
    private final LectureService lectureService;

    @PostMapping("/roadmaps")
    public ResponseEntity<ResponseMessage> uploadRoadmap(@RequestBody RoadMapDto roadMapDto, Principal principal){
        User user=userDetailsService.findUserByEmail(principal.getName());
        String title=roadMapDto.getRoadmapTitle();
        String recommendation=roadMapDto.getRoadmapRecommendation();
        Integer groupId=roadMapService.getMaxRoadmapGroupId();

        List<Long> lectures=roadMapDto.getLectureIds();

        for(int i=0;i<lectures.size();i++){
            Long id=lectures.get(i);
            Lecture lecture=lectureService.findById(id);
            if(lecture==null){
                return new ResponseEntity<>(new ResponseMessage(404,"존재하지 않는 강의에 대한 로드맵 등록 요청 입니다."), HttpStatus.OK);
            }

            RoadMap roadMap=new RoadMap(title,recommendation,lecture,i+1,groupId+1,user);
            roadMapService.saveRoadmap(roadMap);
        }
        return new ResponseEntity<>(new ResponseMessage(201,"새로운 로드맵 등록 성공"),HttpStatus.OK);
    }

}
