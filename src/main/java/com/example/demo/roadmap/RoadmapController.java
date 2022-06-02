package com.example.demo.roadmap;

import com.example.demo.dto.ResponseMessage;
import com.example.demo.lecture.Lecture;
import com.example.demo.lecture.LectureService;
import com.example.demo.like.Like;
import com.example.demo.like.LikeService;
import com.example.demo.review.Review;
import com.example.demo.review.ReviewService;
import com.example.demo.roadmap.dto.*;
import com.example.demo.roadmap.service.RoadmapGroupService;
import com.example.demo.roadmap.service.RoadmapService;
import com.example.demo.user.domain.User;
import com.example.demo.user.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping("/roadmaps/paging")
    public ResponseEntity<ResponseMessage> getAllRoadmaps(@RequestParam(required = false) String keyword,
                                                          @PageableDefault(size = 3,sort = "roadmapGroupId", direction = Sort.Direction.DESC) Pageable pageable){
        List<RoadMapGroup> allRoadmaps=roadmapGroupService.getAllRoadmapGroups();
        if(allRoadmaps.isEmpty()){
            return new ResponseEntity<>(new ResponseMessage(200,"등록된 로드맵이 없습니다."),HttpStatus.OK);
        }
        if(keyword==null){
            return new ResponseEntity<>(ResponseMessage.withData(200,"전체 로드맵 조회 성공",roadmapGroupService.getAllResponseByPageable(pageable)), HttpStatus.OK);
        }

        List<AllRoadmapsResponse> filteredRoadmaps=roadmapGroupService.getAllRoadmapGroupsWithFilter(keyword,pageable);
        if(filteredRoadmaps.isEmpty()){
            return new ResponseEntity<>(new ResponseMessage(200,"조건에 맞는 로드맵이 없습니다."),HttpStatus.OK);
        }
        return new ResponseEntity<>(ResponseMessage.withData(200,"조건에 맞는 로드맵 조회 성공",filteredRoadmaps),HttpStatus.OK);
    }

    @GetMapping("/roadmaps")
    public ResponseEntity<ResponseMessage> getAllRoadmapsNoPage(@RequestParam(required = false) String keyword){
        List<RoadMapGroup> allRoadmaps=roadmapGroupService.getAllRoadmapGroups();
        if(allRoadmaps.isEmpty()){
            return new ResponseEntity<>(new ResponseMessage(200,"등록된 로드맵이 없습니다."),HttpStatus.OK);
        }
        if(keyword==null){
            return new ResponseEntity<>(ResponseMessage.withData(200,"전체 로드맵 조회 성공",roadmapGroupService.getAllResponseWithoutPage()), HttpStatus.OK);
        }

        List<AllRoadmapsResponse> filteredRoadmaps=roadmapGroupService.getAllResponseWithFilterWithoutPage(keyword);
        if(filteredRoadmaps.isEmpty()){
            return new ResponseEntity<>(new ResponseMessage(200,"조건에 맞는 로드맵이 없습니다."),HttpStatus.OK);
        }
        return new ResponseEntity<>(ResponseMessage.withData(200,"조건에 맞는 로드맵 조회 성공",filteredRoadmaps),HttpStatus.OK);
    }

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
            roadmapUploadLectureDto.setHashTags(lectureService.getHashtags(lecture.getLectureId()));
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
        Boolean validateLectures=roadmapGroupService.validateUserLectureForRoadmap(lectures,user);
        if(!validateLectures){
            return new ResponseEntity<>(new ResponseMessage(403,"사용자가 리뷰를 작성하지 않은 강의가 포함되어 있습니다."),HttpStatus.OK);
        }
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
        Boolean validateLectures=roadmapGroupService.validateUserLectureForRoadmap(changedLectureIds,user);
        if(!validateLectures){
            return new ResponseEntity<>(new ResponseMessage(403,"사용자가 리뷰를 작성하지 않은 강의가 포함되어 있습니다."),HttpStatus.OK);
        }
        String changedTitle=roadMapDto.getRoadmapTitle();
        String changedRecommendation=roadMapDto.getRoadmapRecommendation();

        roadmapGroupService.updateRoadmapTitleAndRecommendation(changedTitle,changedRecommendation,group);
        roadmapService.updateRoadmaps(changedLectureIds,group);

        return new ResponseEntity<>(new ResponseMessage(200,"로드맵 수정 성공"),HttpStatus.OK);

    }

    @DeleteMapping("/roadmaps/{roadmapGroupId}")
    public ResponseEntity<ResponseMessage> deleteRoadmap(@PathVariable Long roadmapGroupId){
        RoadMapGroup group=roadmapGroupService.findRoadmapGroupById(roadmapGroupId);
        if(group==null){
            return new ResponseEntity<>(new ResponseMessage(404,"존재하지 않는 로드맵에 대한 요청입니다."),HttpStatus.OK);
        }
        group.setRoadmapGroupStatus(0);
        roadmapGroupService.saveRoadmapGroup(group);
        return new ResponseEntity<>(new ResponseMessage(200,roadmapGroupId+"번 로드맵 삭제 성공"),HttpStatus.OK);
    }

    @GetMapping("/roadmaps/{userId}/company")
    public ResponseEntity<ResponseMessage> checkUserCompanyStatusInRoadmap(@PathVariable Long userId){
        String status= userDetailsService.checkUserCompanyStatus(userId);
        String responseMsg;
        if(status.equals("ask")){
            responseMsg="소속인증 요청 필요";
        }else if(status.equals("no request")){
            responseMsg="소속인증 요청 거부";
        }else{
            responseMsg="소속인증 완료";
        }
        return new ResponseEntity<>(new ResponseMessage(200,responseMsg),HttpStatus.OK);
    }

    @PostMapping("/roadmaps/{userId}/company")
    public String updateUserCompanyStatusInRoadmap(@PathVariable Long userId, @RequestBody UserRoadmapCompanyDto userRoadmapCompanyDto){
        String answer=userRoadmapCompanyDto.getRoadmapCompanyRequestAnswer();
        if(answer.equals("NO_REQUEST")){
            //두 번 다시는 보지 않겠다는 의미 -> 요청 거절
            User user=userDetailsService.findUserById(userId);
            user.updateUserCompany("NO_REQUEST");
            userDetailsService.saveUser(user);
        }
        return "사용자 선택 결과 반영 성공";
    }




}
