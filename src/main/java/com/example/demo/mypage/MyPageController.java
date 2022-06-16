package com.example.demo.mypage;

import com.example.demo.dto.ResponseMessage;
import com.example.demo.lecture.RecommendService;
import com.example.demo.lecture.dto.AllLecturesResponse;
import com.example.demo.mypage.dto.*;
import com.example.demo.study.domain.StudyPost;
import com.example.demo.study.service.StudyPostService;
import com.example.demo.user.UserDetailsServiceImpl;
import com.example.demo.user.domain.User;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Api(tags = {"마이페이지 API"})
@RestController
@RequiredArgsConstructor
@Transactional
@RequestMapping("/users")
public class MyPageController {
    private final UserDetailsServiceImpl userService;
    private final StudyPostService studyPostService;
    private final MyPageService myPageService;
    private final RecommendService recommendService;

    @GetMapping("/{userId}") // 마이페이지 정보 수정 페이지 조회
    public ResponseEntity<ResponseMessage> getProfile(@PathVariable Long userId, Principal principal) {
        User requestedUser = userService.findUserById(userId);
        if(requestedUser==null)
            return new ResponseEntity<>(new ResponseMessage(404,"존재하지 않는 유저"),HttpStatus.NOT_FOUND);

        String email = principal.getName();
        User loginUser = userService.findUserByEmail(email); // 로그인한 사용자 정보 받기
        if(myPageService.checkLoginUser(loginUser, requestedUser)){
            InfoResponse myInfo = myPageService.getProfile(loginUser);
            return new ResponseEntity<>(ResponseMessage.withData(200, "나의 회원정보 조회 성공", myInfo), HttpStatus.OK);
        }
        else { // 다른 사람의 마이페이지 요청한 경우
            if(requestedUser.getPublicProfileStatus() == false)
                return new ResponseEntity<>(new ResponseMessage(403, "비공개 프로필"), HttpStatus.FORBIDDEN);
            MyPageResponse myPageResponse = myPageService.getMyPage(requestedUser);
            return new ResponseEntity<>(ResponseMessage.withData(200, "다른 유저의 회원정보 조회 성공", myPageResponse), HttpStatus.OK);
        }
    }

    @PatchMapping("/{userId}/profiles") // 프로필 공개여부 변경
    public ResponseEntity<ResponseMessage> changeProfileStatus(@PathVariable Long userId, Principal principal) {
        User user = userService.findUserById(userId);
        if(user==null)
            return new ResponseEntity<>(new ResponseMessage(404,"존재하지 않는 유저"),HttpStatus.NOT_FOUND);
        user.updateProfileStatus();
        return new ResponseEntity<>(new ResponseMessage(200, "프로필 공개여부 변경 완료"), HttpStatus.OK);
    }

    @PatchMapping("/{userId}") // 회원정보수정
    public ResponseEntity<ResponseMessage> editProfile(
            @ModelAttribute MyInfoEditDto myInfoEditDto,
            @PathVariable Long userId,
            Principal principal) throws FileUploadException {

        User requestedUser = userService.findUserById(userId);
        if(requestedUser==null)
            return new ResponseEntity<>(new ResponseMessage(404,"존재하지 않는 유저"),HttpStatus.NOT_FOUND);

        String email = principal.getName();
        User loginUser = userService.findUserByEmail(email);
        if(loginUser != requestedUser)
            return new ResponseEntity<>(new ResponseMessage(401, "회원정보 수정 권한 없음_로그인유저와 요청받은 유저번호 불일치"), HttpStatus.UNAUTHORIZED);

        boolean inputOrNot = myPageService.checkPasswordInput(myInfoEditDto);
        if(inputOrNot == true){
            String message = myPageService.checkPassword(myInfoEditDto, loginUser);
            if(message.equals("not equals"))
                return new ResponseEntity<>(new ResponseMessage(401,"비밀번호 확인 오류"),HttpStatus.UNAUTHORIZED);
            else if(message.equals("not match"))
                return new ResponseEntity<>(new ResponseMessage(401,"입력한 비번이 맞지 않음"),HttpStatus.UNAUTHORIZED);
        }

        myPageService.updateProfile(myInfoEditDto, loginUser);
        return new ResponseEntity<>(new ResponseMessage(200,"회원정보 수정 성공"),HttpStatus.OK);
    }

    @DeleteMapping("/{userId}") // 회원탈퇴
    public ResponseEntity<ResponseMessage> resignMembership(@PathVariable Long userId){
        User user = userService.findUserById(userId);
        if(user==null){
            return new ResponseEntity<>(new ResponseMessage(404,"존재하지 않는 회원에 대한 탈퇴 요청"),HttpStatus.OK);
        }else{
            userService.deleteUserById(userId);
            return new ResponseEntity<>(new ResponseMessage(200,"회원탈퇴 성공"),HttpStatus.OK);
        }
    }

    // 좋아요한 강의 조회
    @GetMapping("/{userId}/liked-lectures")
    public ResponseEntity<ResponseMessage> getLikedLectures(@PathVariable("userId") Long userId) {
        User user = userService.findUserById(userId);
        if(user == null)
            return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 유저"), HttpStatus.NOT_FOUND);
        List<LikedLecturesResponse> likedLectures = myPageService.getLikedLectures(user);
        return new ResponseEntity<>(new ResponseMessage(200, "좋아요한 강의 리뷰 조회", likedLectures), HttpStatus.OK);
    }

    // 좋아요한 강의 조회
    @GetMapping("/{userId}/liked-studies")
    public ResponseEntity<ResponseMessage> getLikedStudies(@PathVariable("userId") Long userId) {
        User user = userService.findUserById(userId);
        if(user == null)
            return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 유저"), HttpStatus.NOT_FOUND);
        List<LikedStudiesResponse> likedStudies = myPageService.getLikedStudies(user);
        return new ResponseEntity<>(new ResponseMessage(200, "좋아요한 스터디 조회", likedStudies), HttpStatus.OK);
    }

    // 좋아요한 로드맵 조회
    @GetMapping("/{userId}/liked-roadmaps")
    public ResponseEntity<ResponseMessage> getLikedRoadmaps(@PathVariable("userId") Long userId) {
        User user = userService.findUserById(userId);
        if(user == null)
            return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 유저"), HttpStatus.NOT_FOUND);
        List<LikedRoadmapsResponse> likedRoadmaps = myPageService.getLikedRoadmaps(user);
        return new ResponseEntity<>(new ResponseMessage(200, "좋아요한 로드맵 조회", likedRoadmaps), HttpStatus.OK);
    }

    // 작성한 강의리뷰 조회
    @GetMapping("/{userId}/reviews")
    public ResponseEntity<ResponseMessage> getMyReviews(@PathVariable("userId") Long userId) {
        User user = userService.findUserById(userId);
        if(user == null)
            return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 유저"), HttpStatus.NOT_FOUND);
        List<MyReviewsResponse> myReviews = myPageService.getMyReviews(user);
        return new ResponseEntity<>(new ResponseMessage(200, "작성한 강의리뷰 조회", myReviews), HttpStatus.OK);
    }

    // 작성한 스터디 조회
    @GetMapping("/{userId}/studies")
    public ResponseEntity<ResponseMessage> getMyStudies(@PathVariable("userId") Long userId) {
        User user = userService.findUserById(userId);
        if(user == null)
            return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 유저"), HttpStatus.NOT_FOUND);
        List<MyStudiesResponse> myStudies = myPageService.getMyStudies(user);
        return new ResponseEntity<>(new ResponseMessage(200, "작성한 스터디 조회", myStudies), HttpStatus.OK);
    }

    // 스터디 상태변경 _ 마이페이지
    @PatchMapping("/{userId}/studies/{studyId}")
    public ResponseEntity<ResponseMessage> changeRecruitStatus(@PathVariable("userId") Long userId, @PathVariable("studyId") Long studyId) {
        User user = userService.findUserById(userId);
        if (user == null)
            return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 유저"), HttpStatus.NOT_FOUND);
        StudyPost study = studyPostService.findStudyPostById(studyId);
        if (study == null)
            return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 스터디"), HttpStatus.NOT_FOUND);
        if (study.getStudyRecruitStatus() == 1) { // 모집중이라면
            study.updateRecruitStatus(0);
            return new ResponseEntity<>(new ResponseMessage(200, "스터디 모집상태 모집완료로 변경"), HttpStatus.OK);
        } else {
            study.updateRecruitStatus(1);
            return new ResponseEntity<>(new ResponseMessage(200, "스터디 모집상태 모집중으로 변경"), HttpStatus.OK);
        }
    }

    // 작성한 로드맵 조회
    @GetMapping("/{userId}/roadmaps")
    public ResponseEntity<ResponseMessage> getMyRoadmaps(@PathVariable("userId") Long userId) {
        User user = userService.findUserById(userId);
        if(user == null)
            return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 유저"), HttpStatus.NOT_FOUND);
        List<MyRoadmapsResponse> myRoadmaps = myPageService.getMyRoadmaps(user);
        return new ResponseEntity<>(new ResponseMessage(200, "작성한 로드맵 조회", myRoadmaps), HttpStatus.OK);
    }

    // 추천된 강의 목록 조회
    @GetMapping("/{userId}/recommended-lectures")
    public ResponseEntity<ResponseMessage> getRecommendedData(@PathVariable("userId") Long userId) {
        User user = userService.findUserById(userId);
        List<AllLecturesResponse> recommendedLectures = recommendService.getRecommendedData(user);
        return new ResponseEntity<>(ResponseMessage.withData(200, "추천된 강의 목록", recommendedLectures), HttpStatus.OK);
    }

    @GetMapping("/{userId}/requested-lectures")
    public ResponseEntity<ResponseMessage> getRequestedLectures(@PathVariable("userId") Long userId){
        User user = userService.findUserById(userId);
        List<RequestedLectureResponse> myRequestedLectures = myPageService.getMyRequestedLectures(user);
        return new ResponseEntity<>(ResponseMessage.withData(200,"강의 등록 요청 내역",myRequestedLectures),HttpStatus.OK);
    }
}
