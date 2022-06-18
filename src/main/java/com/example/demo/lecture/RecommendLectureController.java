package com.example.demo.lecture;

import com.example.demo.dto.ResponseMessage;
import com.example.demo.lecture.dto.AllLecturesForRecommendResponse;
import com.example.demo.lecture.dto.AllLecturesResponse;
import com.example.demo.lecture.dto.LikedLecturesForRecommendResponse;
import com.example.demo.user.UserDetailsServiceImpl;
import com.example.demo.user.domain.User;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@Api(tags={"추천알고리즘 API"})
@RestController
@RequiredArgsConstructor
@Transactional
@RequestMapping("/recommended-lectures")
public class RecommendLectureController {
    private final UserDetailsServiceImpl userDetailsService;
    private final RecommendService recommendService;

    // 추천 알고리즘 전송용 메소드 - 전체 강의 데이터에 대해서
    @GetMapping("/all")
    public ResponseEntity<ResponseMessage> sendDataForRecommendation() {
        List<AllLecturesForRecommendResponse> recLectures = recommendService.manageAllData();
        return new ResponseEntity<>(ResponseMessage.withData(200, "추천 알고리즘용 모든 강의 데이터 전송 완료", recLectures), HttpStatus.OK);
    }

    // 추천 알고리즘 전송용 메소드 - 사용자가 좋아요한 강의 데이터에 대해서
    @GetMapping("/liked/{userId}")
    public ResponseEntity<ResponseMessage> sendLikedData(@PathVariable("userId") Long userId) {
        User user = userDetailsService.findUserById(userId);
        List<LikedLecturesForRecommendResponse> recLikedLectures = recommendService.manageLikedData(user);
        return new ResponseEntity<>(ResponseMessage.withData(200, "추천 알고리즘용 좋아요한 강의 데이터 강의 전송 완료", recLikedLectures), HttpStatus.OK);
    }

    // 추천 강의 조회 메소드
    @GetMapping("")
    public ResponseEntity<ResponseMessage> getRecommendedLectures(Principal principal) {
        String email = principal.getName();
        User user = userDetailsService.findUserByEmail(email);
        List<AllLecturesResponse> recommendedLectures = recommendService.getRecommendedData(user);
        if(recommendedLectures == null)
            return new ResponseEntity<>(new ResponseMessage(200, "추천 강의 없음"), HttpStatus.OK);
        return new ResponseEntity<>(ResponseMessage.withData(200, "추천된 강의 조회", recommendedLectures), HttpStatus.OK);
    }

}
