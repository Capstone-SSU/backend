package com.example.demo.roadmap;

import com.example.demo.dto.ResponseMessage;
import com.example.demo.lecture.Lecture;
import com.example.demo.lecture.LectureService;
import com.example.demo.like.Like;
import com.example.demo.like.LikeService;
import com.example.demo.review.Review;
import com.example.demo.review.ReviewService;
import com.example.demo.roadmap.dto.DetailRoadmapLectureResponse;
import com.example.demo.roadmap.dto.DetailRoadmapResponse;
import com.example.demo.roadmap.dto.RoadMapDto;
import com.example.demo.roadmap.dto.RoadmapUploadLectureDto;
import com.example.demo.roadmap.service.RoadmapGroupService;
import com.example.demo.roadmap.service.RoadmapService;
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
public class RoadmapController {
    private final RoadmapGroupService roadmapGroupService;
    private final RoadmapService roadmapService;
    private final UserDetailsServiceImpl userDetailsService;
    private final LectureService lectureService;
    private final ReviewService reviewService;
    private final LikeService likeService;

    @GetMapping("/roadmaps/lectures/{userId}")
    public ResponseEntity<ResponseMessage> getAllLecturesForRoadmap(@PathVariable Long userId){
        User user=userDetailsService.findUserById(userId);
        if(user==null){
            return new ResponseEntity<>(new ResponseMessage(404,"존재하지 않는 사용자에 대한 요청입니다."), HttpStatus.OK);
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

    @PostMapping("/roadmaps")
    public ResponseEntity<ResponseMessage> uploadRoadmap(@RequestBody RoadMapDto roadMapDto, Principal principal){
        User user=userDetailsService.findUserByEmail(principal.getName());
        String title=roadMapDto.getRoadmapTitle();
        String recommendation=roadMapDto.getRoadmapRecommendation();

        List<Long> lectures=roadMapDto.getLectureIds();
        RoadMapGroup roadMapGroup=new RoadMapGroup(title,recommendation,user);
        RoadMapGroup savedGroup=roadmapGroupService.saveRoadmapGroup(roadMapGroup);

        for(int i=0;i<lectures.size();i++){
            Long id=lectures.get(i);
            Lecture lecture=lectureService.findById(id);

            RoadMap roadMap=new RoadMap(lecture,i+1,savedGroup);
            roadmapService.saveRoadmap(roadMap);
        }
        return new ResponseEntity<>(new ResponseMessage(201,"새로운 로드맵 등록 성공"),HttpStatus.OK);
    }

    @GetMapping("/roadmaps/{roadmapGroupId}")
    public ResponseEntity<ResponseMessage> getRoadmap(@PathVariable Long roadmapGroupId,Principal principal) {
        RoadMapGroup group=roadmapGroupService.findRoadmapGroupById(roadmapGroupId);

        User user=userDetailsService.findUserByEmail(principal.getName());
        if(group==null){
            return new ResponseEntity<>(new ResponseMessage(404,"존재하지 않는 로드맵에 대한 요청입니다."),HttpStatus.OK);
        }

        List<RoadMap> roadmaps=roadmapService.getAllRoadMapsByGroup(group);
        User roadmapWriter=group.getUser();
        DetailRoadmapResponse detailRoadmapResponse = roadmapGroupService.getDetailRoadmapResponse(user,group,roadmapWriter);
        List<DetailRoadmapLectureResponse> detailRoadmapLectureResponses=roadmapService.getAllDetailLecturesInRoadmap(roadmaps,roadmapWriter);
        detailRoadmapResponse.setLectures(detailRoadmapLectureResponses);

        return new ResponseEntity<>(ResponseMessage.withData(200,"상세 로드맵 조회 성공",detailRoadmapResponse),HttpStatus.OK);
    }

    @PostMapping("/roadmaps/{roadmapGroupId}/likes")
    public ResponseEntity<ResponseMessage> setLikeOnRoadmap(@PathVariable Long roadmapGroupId,Principal principal){
        RoadMapGroup group=roadmapGroupService.findRoadmapGroupById(roadmapGroupId);

        if(group==null){
            return new ResponseEntity<>(new ResponseMessage(404,"존재하지 않는 로드맵에 대한 요청입니다."),HttpStatus.OK);
        }

        User user=userDetailsService.findUserByEmail(principal.getName());
        Like foundLike = likeService.findLikeByRoadmapAndUser(user,group);
        if(foundLike==null){
            Like like=new Like(group,user);
            likeService.saveLike(like);
            return new ResponseEntity<>(new ResponseMessage(201,"좋아요가 등록되었습니다."),HttpStatus.CREATED);
        }else{
            foundLike.changeLikeStatus(foundLike.getLikeStatus()==0?1:0);
            likeService.saveLike(foundLike);
            return new ResponseEntity<>(new ResponseMessage(200,"좋아요 상태 변경 성공"),HttpStatus.OK);
        }
    }

    @PatchMapping("/roadmaps/{roadmapGroupId}")
    public ResponseEntity<ResponseMessage> modifyRoadmap(@PathVariable Long roadmapGroupId, @RequestBody RoadMapDto roadMapDto,Principal principal){

        RoadMapGroup group=roadmapGroupService.findRoadmapGroupById(roadmapGroupId);
        if(group==null){
            return new ResponseEntity<>(new ResponseMessage(404,"존재하지 않는 로드맵에 대한 요청입니다."),HttpStatus.OK);
        }
        User user=userDetailsService.findUserByEmail(principal.getName());

        List<Long> changedLectureIds=roadMapDto.getLectureIds();
        String changedTitle=roadMapDto.getRoadmapTitle();
        String changedRecommendation=roadMapDto.getRoadmapRecommendation();

        roadmapGroupService.updateRoadmapTitleAndRecommendation(changedTitle,changedRecommendation,group);
        roadmapService.updateRoadmaps(changedLectureIds,group);

        return new ResponseEntity<>(new ResponseMessage(200,"로드맵 수정 성공"),HttpStatus.OK);

    }




}
