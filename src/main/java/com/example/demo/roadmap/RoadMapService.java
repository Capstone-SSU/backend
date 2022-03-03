package com.example.demo.roadmap;

import com.example.demo.lecture.Lecture;
import com.example.demo.lecture.LectureService;
import com.example.demo.like.Like;
import com.example.demo.like.LikeService;
import com.example.demo.review.Review;
import com.example.demo.review.ReviewService;
import com.example.demo.roadmap.dto.DetailRoadmapLectureResponse;
import com.example.demo.roadmap.dto.DetailRoadmapResponse;
import com.example.demo.roadmap.repository.RoadMapRepository;
import com.example.demo.user.User;
import com.example.demo.user.UserDetailsServiceImpl;
import com.example.demo.user.dto.SimpleUserDto;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class RoadMapService {
    private final RoadMapRepository roadMapRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final LectureService lectureService;
    private final ReviewService reviewService;
    private final LikeService likeService;

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

    //로드맵 그룹아이디를 기반으로 하나의 로드맵을 찾아옴 -> order 순서대로 오름차순 정렬
    public List<RoadMap> getAllRoadMapsByGroup(Integer roadmapGroupId){
        List<RoadMap> roadmaps=roadMapRepository.findAllRoadmapsByGroupId(roadmapGroupId);
        return roadmaps;
    }

    public DetailRoadmapResponse getDetailRoadmapResponse(List<RoadMap> originRoadmaps, User user){
        RoadMap firstRoadmap=originRoadmaps.get(0);
        Integer groupId=firstRoadmap.getRoadmapGroupId();
        DetailRoadmapResponse detailRoadmapResponse=new DetailRoadmapResponse();
        BeanUtils.copyProperties(firstRoadmap,detailRoadmapResponse); //로드맵 정보는 첫번째 거에서만 가져와도 OK
        detailRoadmapResponse.setIsLikedByUser(getUserRoadmapLikedStatus(groupId,user));
        detailRoadmapResponse.setIsThisUserRoadmapWriter(firstRoadmap.getUser().getUserId()==user.getUserId());
        detailRoadmapResponse.setRoadmapWriter(userDetailsService.getSimpleUserDto(user));
        detailRoadmapResponse.setLikeCount(getLikeCountOnRoadmap(firstRoadmap.getRoadmapGroupId()));
        List<DetailRoadmapLectureResponse> lectures=new ArrayList<>();
        for(RoadMap roadMap:originRoadmaps){
            DetailRoadmapLectureResponse lectureResponse=new DetailRoadmapLectureResponse();
            Lecture lecture=roadMap.getLecture();
            BeanUtils.copyProperties(lecture,lectureResponse);
            lectureResponse.setLectureHashtags(lectureService.getBestHashtags(lecture));
            lectureResponse.setLectureAvgRate(lectureService.getAvgRate(lecture));
            Review review=reviewService.findByUserAndLecture(user,lecture);
            lectureResponse.setLectureReviewTitle(review.getCommentTitle());
            lectureResponse.setLectureReviewContent(review.getComment());
            lectures.add(lectureResponse);
        }
        detailRoadmapResponse.setLectures(lectures);
        return detailRoadmapResponse;
    }

    public Integer getLikeCountOnRoadmap(Integer roadmapGroupId){
        RoadMap roadMap=roadMapRepository.findAllRoadmapsByGroupId(roadmapGroupId).get(0);
        Integer count=roadMap.getLikes().size();
        return count;
    }

    public Boolean getUserRoadmapLikedStatus(Integer roadmapGroupId,User user){
        RoadMap roadMap=getAllRoadMapsByGroup(roadmapGroupId).get(0);
        Like like=likeService.findLikeByRoadmapAndUser(roadMap,user);
        return like != null && like.getLikeStatus() == 1;

    }



}
