package com.example.demo.mypage;
import com.example.demo.dto.ResponseMessage;
import com.example.demo.mypage.dto.LikedLecturesResponse;
import com.example.demo.mypage.dto.LikedStudiesResponse;
import com.example.demo.mypage.dto.MyReviewsResponse;
import com.example.demo.user.User;
import com.example.demo.user.UserDetailsServiceImpl;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = {"마이페이지 API"})
@RestController
@RequiredArgsConstructor
@Transactional
@RequestMapping("/users")
public class MyPageController {
    private final UserDetailsServiceImpl userDetailsService;
    private final MyPageService myPageService;

    // 좋아요한 강의 조회
    @GetMapping("/{userId}/liked-lectures")
    public ResponseEntity<ResponseMessage> getLikedLectures(@PathVariable("userId") String userId) {
        Long id = Long.parseLong(userId);
        User user = userDetailsService.findUserById(id);
        if(user == null)
            return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 유저"), HttpStatus.NOT_FOUND);
        List<LikedLecturesResponse> likedLectures = myPageService.getLikedLectures(user);
        return new ResponseEntity<>(new ResponseMessage(200, "좋아요한 강의 리뷰 조회", likedLectures), HttpStatus.OK);
    }

    // 좋아요한 강의 조회
    @GetMapping("/{userId}/liked-studies")
    public ResponseEntity<ResponseMessage> getLikedStudies(@PathVariable("userId") String userId) {
        Long id = Long.parseLong(userId);
        User user = userDetailsService.findUserById(id);
        if(user == null)
            return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 유저"), HttpStatus.NOT_FOUND);
        List<LikedStudiesResponse> likedStudies = myPageService.getLikedStudies(user);
        return new ResponseEntity<>(new ResponseMessage(200, "좋아요한 스터디 조회", likedStudies), HttpStatus.OK);
    }

    // 작성한 강의리뷰 조회
    @GetMapping("/{userId}/reviews")
    public ResponseEntity<ResponseMessage> getMyReviews(@PathVariable("userId") String userId) {
        Long id = Long.parseLong(userId);
        User user = userDetailsService.findUserById(id);
        if(user == null)
            return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 유저"), HttpStatus.NOT_FOUND);
        List<MyReviewsResponse> myReviews = myPageService.getMyStudies(user);
        return new ResponseEntity<>(new ResponseMessage(200, "작성한 강의리뷰 조회", myReviews), HttpStatus.OK);
    }
}
