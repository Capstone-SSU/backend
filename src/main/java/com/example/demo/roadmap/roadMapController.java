package com.example.demo.roadmap;

import com.example.demo.dto.ResponseMessage;
import com.example.demo.lecture.Lecture;
import com.example.demo.lecture.LectureService;
import com.example.demo.like.Like;
import com.example.demo.like.LikeService;
import com.example.demo.review.Review;
import com.example.demo.review.ReviewService;
import com.example.demo.roadmap.dto.DetailRoadmapResponse;
import com.example.demo.roadmap.dto.RoadMapDto;
import com.example.demo.roadmap.dto.RoadmapUploadLectureDto;
import com.example.demo.user.User;
import com.example.demo.user.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
public class roadMapController {
    private final RoadMapService roadMapService;
    private final UserDetailsServiceImpl userDetailsService;
    private final LectureService lectureService;
    private final ReviewService reviewService;
    private final LikeService likeService;

    @PostMapping("/roadmaps") //넘어온 lectures가 null이거나 empty면 다른 response 만들기
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

    @GetMapping("/roadmaps/lectures/{userId}")
    public ResponseEntity<ResponseMessage> getAllLecturesForRoadmap(@PathVariable Long userId){
        User user=userDetailsService.findUserById(userId);
        if(user==null){
            return new ResponseEntity<>(new ResponseMessage(404,"존재하지 않는 사용자에 대한 요청입니다."),HttpStatus.OK);
        }
        //이 사용자가 작성한 모든 리뷰들 가져오기 -> 거기서 lecture 컬럼 빼오기
        List<Review> reviews=reviewService.findAllReviewsByUser(user);
        if(reviews.isEmpty()){
            return new ResponseEntity<>(ResponseMessage.withData(200,"리뷰를 남긴 강의가 존재하지 않습니다.",reviews),HttpStatus.OK);
        }
        List<RoadmapUploadLectureDto> userLectureList=new ArrayList<>();
        for(Review review:reviews){
            Lecture lecture=review.getLecture();
            RoadmapUploadLectureDto roadmapUploadLectureDto =new RoadmapUploadLectureDto();
            BeanUtils.copyProperties(lecture, roadmapUploadLectureDto);
            roadmapUploadLectureDto.setHashTags(lectureService.getBestHashtags(lecture));
            userLectureList.add(roadmapUploadLectureDto);
        }
        return new ResponseEntity<>(ResponseMessage.withData(200,"사용자가 등록한 강의 목록입니다.",userLectureList),HttpStatus.OK);
    }

    @GetMapping("/roadmaps/{roadmapGroupId}")
    public ResponseEntity<ResponseMessage> getRoadmap(@PathVariable Integer roadmapGroupId,Principal principal) {
        List<RoadMap> roadmaps=roadMapService.getAllRoadMapsByGroup(roadmapGroupId);
        User user=userDetailsService.findUserByEmail(principal.getName());
        if(roadmaps.isEmpty()){
            return new ResponseEntity<>(new ResponseMessage(404,"존재하지 않는 로드맵에 대한 요청입니다."),HttpStatus.OK);
        }
        DetailRoadmapResponse detailRoadmapResponse = roadMapService.getDetailRoadmapResponse(roadmaps, user);

        return new ResponseEntity<>(ResponseMessage.withData(200,"상세 로드맵 조회 성공",detailRoadmapResponse),HttpStatus.OK);
    }
    //전체 로드맵 목록을 돌려줄 때, 로드맵의 groupId를 보내주어야함!

    //해야할 것: 1. 좋아요 API 추가 2. 메소드로 분리시키는게 나은 것들 분리시키기 (마이페이지 고려)
    @PostMapping("/roadmaps/{roadmapGroupId}/likes")
    public ResponseEntity<ResponseMessage> setLikeOnRoadmap(@PathVariable Integer roadmapGroupId,Principal principal){
        List<RoadMap> roadmaps=roadMapService.getAllRoadMapsByGroup(roadmapGroupId);
        User user=userDetailsService.findUserByEmail(principal.getName());
        if(roadmaps.isEmpty()){
            return new ResponseEntity<>(new ResponseMessage(404,"존재하지 않는 로드맵에 대한 요청입니다."),HttpStatus.OK);
        }
        RoadMap roadMap=roadmaps.get(0);
        Like foundLike = likeService.findLikeByRoadmapAndUser(roadMap, user); //같은 groupId를 가진 로드맵들에서, 첫 번째 데이터를 가지고 좋아요 상태 판별
        if(foundLike==null){
             // 이 로드맵과 동일한 groupId를 가진 애들은 다 update 해주어야함 -> ex) groupId==3인 로드맵들 id가 3,4,5라면? likes 테이블에 lectureId 3,4,5에 대해 모두 새 데이터 저장됨
            for(RoadMap map:roadmaps){
                Like like=new Like(map,user);
                likeService.saveLike(like);
            }
            return new ResponseEntity<>(new ResponseMessage(201,"좋아요가 등록되었습니다."),HttpStatus.CREATED);
        }else{
            for(RoadMap map:roadmaps){
                Like like=likeService.findLikeByRoadmapAndUser(map,user);
                like.setLikeStatus(like.getLikeStatus()==1?0:1);
                likeService.saveLike(like);
            }
            return new ResponseEntity<>(new ResponseMessage(200,"좋아요 상태 변경 성공"),HttpStatus.OK);
        }
    }

}
