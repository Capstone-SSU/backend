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
import com.example.demo.user.User;
import com.example.demo.user.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
@AllArgsConstructor
public class roadMapController {
    private final RoadMapService roadMapService;
    private final UserDetailsServiceImpl userDetailsService;
    private final LectureService lectureService;
    private final ReviewService reviewService;
    private final LikeService likeService;

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

    @PatchMapping("/roadmaps/{roadmapGroupId}")
    public ResponseEntity<ResponseMessage> modifyRoadmap(@PathVariable Integer roadmapGroupId, @RequestBody RoadMapDto roadMapDto,Principal principal){
        //제목, 추천대상, 로드맵 구성 변경가능 -> 로드맵 구성 변경,,,을,,, 어떻게,,,하지,,,<?
        List<RoadMap> roadmaps=roadMapService.getAllRoadMapsByGroup(roadmapGroupId);
        User user=userDetailsService.findUserByEmail(principal.getName());
        if(roadmaps.isEmpty()){
            return new ResponseEntity<>(new ResponseMessage(404,"존재하지 않는 로드맵에 대한 요청입니다."),HttpStatus.OK);
        }
        RoadMap sampleRoadmap=roadmaps.get(0);
        LocalDateTime roadmapCreatedDate = sampleRoadmap.getRoadmapCreatedDate();
        List<Like> likeList=sampleRoadmap.getLikes(); //로드맵 초기 생성 날짜와 좋아요 목록을 새롭게 추가된 애들한테도 똑같이 반영해주어야함
        List<Long> changedLectureIds=roadMapDto.getLectureIds();

        //roadmap 들 중에서 동일 lectureId를 가지는 애들을 찾고, update 하는 쿼리문 (이렇게 안하면 이중포문을 써야할 것 같음)
        Iterator<RoadMap> itr=roadmaps.iterator();
        while(itr.hasNext()){
            RoadMap roadMap=itr.next();
            for(int i=1;i<changedLectureIds.size()+1;i++){
                if(roadMap.getLecture().getLectureId()==changedLectureIds.get(i-1)){
                    roadMap.updateRoadmapLectureOrder(i);
                    roadMap.setRoadmapTitle(roadMapDto.getRoadmapTitle());
                    roadMap.setRoadmapRecommendation(roadMapDto.getRoadmapRecommendation());
                    roadMapService.saveRoadmap(roadMap);
                    changedLectureIds.set(i-1,-1L);
                    itr.remove();
                    break;
                }
            }
        }

        if(!roadmaps.isEmpty()){
            for(RoadMap roadMap:roadmaps){
                roadMap.setRoadmapStatus(0); //사용자가 지워야할 강의들은 0으로 세팅
                roadMapService.saveRoadmap(roadMap);
            }
        }

        //이제 새롭게 변경된 로드맵 강의 구성들 중에서, 로드맵에 새롭게 추가되어야할 것들을 작업
        //일단 동일한 groupId와 lectureId를 가진 것 중에 status 가 0인게 있는지 확인 -> 있다면 status를 1로 변경하고 save
        //없다면 그냥 새롭게 roadmap 객체 만들어서 save
        for(int i=1;i<changedLectureIds.size()+1;i++){
            Long lectureId=changedLectureIds.get(i-1);
            if(lectureId==-1L){
                continue;
            }
            RoadMap roadMap=roadMapService.getRoadmapByLectureIdAndGroup(lectureId,roadmapGroupId);
            if(roadMap!=null){ //지워진 애가 있었다면? 순서변경 + 살리기
                roadMap.updateRoadmapLectureOrder(i);
                roadMap.setRoadmapTitle(roadMapDto.getRoadmapTitle());
                roadMap.setRoadmapRecommendation(roadMapDto.getRoadmapRecommendation());
                roadMap.setRoadmapStatus(1);
            }else{
                //이전에 추가된 전적이 없었다면, 새롭게 만들어서 저장
                Lecture lecture=lectureService.findById(lectureId);
                roadMap=new RoadMap(roadMapDto.getRoadmapTitle(),roadMapDto.getRoadmapRecommendation(),lecture,i,roadmapGroupId,user);
                roadMapService.saveRoadmap(roadMap);
            }
            roadMap.setRoadmapCreatedDate(roadmapCreatedDate);
            //이 로드맵에 대해 좋아요 누른 전체 목록 -> 모두 동일 user, 동일 likeStatus 로 세팅해주기
            for(Like originLike:likeList){
                User likedUser=originLike.getUser();
                Integer likeStatus=originLike.getLikeStatus();
                //이 user와 roadmap에 대해 동일한 like가 있었는지 찾고, 없었다면 like를 새롭게 추가
                Like foundLike = likeService.findLikeByRoadmapAndUser(roadMap, likedUser);
                if(foundLike!=null){
                    foundLike.setLikeStatus(likeStatus);
                    likeService.saveLike(foundLike);
                }else{
                    //만약 동일한 roadmapId와 user에 대해 like가 없었다면, 새롭게 like 생성
                    Like like=new Like(roadMap,likedUser);
                    like.setLikeStatus(likeStatus);
                    likeService.saveLike(like);
                }
            }
            roadMapService.saveRoadmap(roadMap);
        }

        return new ResponseEntity<>(new ResponseMessage(200,"로드맵이 수정 성공"),HttpStatus.OK);

    }

    @DeleteMapping("/roadmaps/{roadmapGroupId}")
    public ResponseEntity<ResponseMessage> deleteRoadmap(@PathVariable Integer roadmapGroupId,Principal principal){
        String email=principal.getName();
        List<RoadMap> roadMaps=roadMapService.getAllRoadMapsByGroup(roadmapGroupId);
        if(roadMaps.isEmpty())
            return new ResponseEntity<>(new ResponseMessage(404,"존재하지 않는 로드맵에 대한 삭제 요청입니다."),HttpStatus.OK);
        String writerEmail=roadMaps.get(0).getUser().getUserEmail();
        if(!email.equals(writerEmail))
            return new ResponseEntity<>(new ResponseMessage(403,roadmapGroupId+"번 로드맵에 삭제 권한이 없는 사용자 입니다."),HttpStatus.OK);
        for(RoadMap roadMap:roadMaps){
            roadMap.setRoadmapStatus(0);
            roadMapService.saveRoadmap(roadMap);
        }
        return new ResponseEntity<>(new ResponseMessage(200, roadmapGroupId+"번 로드맵 삭제 성공"),HttpStatus.OK);
    }

}
