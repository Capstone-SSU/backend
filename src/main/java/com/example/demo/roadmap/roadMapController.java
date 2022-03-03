package com.example.demo.roadmap;

import com.example.demo.dto.ResponseMessage;
import com.example.demo.lecture.Lecture;
import com.example.demo.lecture.LectureService;
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
        //강의제목, 강의별점, 별점숫자, 해시태그 3개, 강의별 리뷰 제목과 내용 (로드맵 등록자가 작성한), 강의별 id (api 보낼 수 있게), 강의 썸네일
        //로드맵 제목, 좋아요 수, 이 사용자가 좋아요를 눌렀는가 (로드맵에), 이 사용자가 로드맵 작성자인가St
        //작성자 이름, 작성자 company, 추천대상, 작성자 프로필 이미지
        //강의 데이터 보내줄 때는 로드맵에 담긴 순서대로 담아서 보내주기
        List<RoadMap> roadmaps=roadMapService.getAllRoadMapsByGroup(roadmapGroupId);
        User user=userDetailsService.findUserByEmail(principal.getName());
        if(roadmaps.isEmpty()){
            return new ResponseEntity<>(new ResponseMessage(404,"존재하지 않는 로드맵에 대한 요청입니다."),HttpStatus.OK);
        }
        DetailRoadmapResponse detailRoadmapResponse = roadMapService.getDetailRoadmapResponse(roadmaps, user);

        return new ResponseEntity<>(ResponseMessage.withData(200,"상세 로드맵 조회 성공",detailRoadmapResponse),HttpStatus.OK);
    }
    //전체 로드맵 목록을 돌려줄 때, 로드맵의 groupId를 보내주어야함!

}
