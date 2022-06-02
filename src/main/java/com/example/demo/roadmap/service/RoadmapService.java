package com.example.demo.roadmap.service;

import com.example.demo.lecture.Lecture;
import com.example.demo.lecture.LectureService;
import com.example.demo.review.Review;
import com.example.demo.review.ReviewService;
import com.example.demo.roadmap.RoadMap;
import com.example.demo.roadmap.RoadMapGroup;
import com.example.demo.roadmap.dto.DetailRoadmapLectureResponse;
import com.example.demo.roadmap.repository.RoadmapRepository;
import com.example.demo.user.domain.User;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class RoadmapService {
    private final RoadmapRepository roadmapRepository;
    private final LectureService lectureService;
    private final ReviewService reviewService;

    public void saveRoadmap(RoadMap roadMap){
        roadmapRepository.save(roadMap);
    }

    public List<RoadMap> getAllRoadMapsByGroup(RoadMapGroup group){
        return roadmapRepository.findAllRoadmapsByGroup(group);
    }

    public List<DetailRoadmapLectureResponse> getAllDetailLecturesInRoadmap(List<RoadMap> originRoadmaps, User roadmapWriter){
        List<DetailRoadmapLectureResponse> lectures=new ArrayList<>();

        for(RoadMap roadMap:originRoadmaps){
            DetailRoadmapLectureResponse lectureResponse=new DetailRoadmapLectureResponse();
            Lecture lecture=roadMap.getLecture();
            BeanUtils.copyProperties(lecture,lectureResponse);
            lectureResponse.setLectureHashtags(lectureService.getHashtags(lecture.getLectureId()));
            lectureResponse.setLectureAvgRate(lecture.getAvgRate());
            Review review=reviewService.findByUserAndLecture(roadmapWriter,lecture); //로드맵 작성자의 리뷰를 찾아야함
            lectureResponse.setLectureReviewTitle(review.getCommentTitle());
            lectureResponse.setLectureReviewContent(review.getComment());
            lectures.add(lectureResponse);
        }

        return lectures;
    }

    public void updateRoadmaps(List<Long> changedLectureIds,RoadMapGroup group){
        List<Lecture> lectures=new ArrayList<>();
        for(Long lectureId:changedLectureIds){
            Lecture lecture=lectureService.findById(lectureId);
            lectures.add(lecture);
        }
        roadmapRepository.updateExistingRoadmaps(lectures,group);
        List<RoadMap> inDatabaseRoadmaps=roadmapRepository.findRoadmapsByGroupAndLectures(group,lectures);
        if(inDatabaseRoadmaps.isEmpty())
            return;
        List<Lecture> inDatabaseLectures=new ArrayList<>();
        for(RoadMap map:inDatabaseRoadmaps){
            inDatabaseLectures.add(map.getLecture());
        }

        for(Lecture lecture:inDatabaseLectures){
            if(lectures.contains(lecture)){
                int index=lectures.indexOf(lecture);
                lectures.set(index,null); //이미 디비에 있는 애들은 새로 추가할 필요가 없으므로 null로 둔다.
            }
        }

        for(int i=0;i<lectures.size();i++){
            Lecture lecture=lectures.get(i);
            if(lecture==null)
                continue;
            RoadMap roadMap=new RoadMap(lecture,i+1,group); //null 이 아닌 것만 새롭게 디비에 추가
            saveRoadmap(roadMap);
        }

    }


}
