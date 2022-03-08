package com.example.demo.roadmap.service;

import com.example.demo.lecture.Lecture;
import com.example.demo.lecture.LectureService;
import com.example.demo.review.Review;
import com.example.demo.review.ReviewService;
import com.example.demo.roadmap.RoadMap;
import com.example.demo.roadmap.RoadMapGroup;
import com.example.demo.roadmap.dto.DetailRoadmapLectureResponse;
import com.example.demo.roadmap.repository.RoadmapRepository;
import com.example.demo.user.User;
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
            lectureResponse.setLectureHashtags(lectureService.getBestHashtags(lecture));
            lectureResponse.setLectureAvgRate(lectureService.getAvgRate(lecture));
            Review review=reviewService.findByUserAndLecture(roadmapWriter,lecture); //로드맵 작성자의 리뷰를 찾아야함
            lectureResponse.setLectureReviewTitle(review.getCommentTitle());
            lectureResponse.setLectureReviewContent(review.getComment());
            lectures.add(lectureResponse);
        }

        return lectures;
    }


}
