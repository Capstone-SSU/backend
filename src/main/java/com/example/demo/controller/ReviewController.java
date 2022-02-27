package com.example.demo.controller;

import com.example.demo.domain.Review;
import com.example.demo.user.User;
import com.example.demo.dto.LectureDto;
import com.example.demo.dto.ResponseMessage;
import com.example.demo.dto.ReviewDto;
import com.example.demo.user.UserDetailsServiceImpl;
import com.example.demo.service.ReviewService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import java.security.Principal;

@Api(tags = { "Review"})
@RestController
@RequiredArgsConstructor
@Transactional
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final UserDetailsServiceImpl userDetailsService;
    private final EntityManager em;

    @PostMapping("")
    public ResponseEntity<ResponseMessage> createReview(@RequestBody LectureDto lectureDto, Principal principal) {
        String email = principal.getName(); //
        // 현재로그인한 사용자 아이디 가져오기
        User user = userDetailsService.findUserByEmail(email);
        System.out.println("user = " + user);
        int rate = lectureDto.getRate().intValue();
        String commentTitle = lectureDto.getCommentTitle();
        String comment = lectureDto.getComment();

        return new ResponseEntity<>(new ResponseMessage(201, "강의 리뷰가 등록되었습니다."), HttpStatus.CREATED);
    }

    @PatchMapping("/{reviewId}") // 리뷰 수정
    public ResponseEntity<ResponseMessage> updateReview(@PathVariable("reviewId") Long reviewId, @RequestBody ReviewDto reviewDto, Principal principal) {
        Review review = reviewService.findByReviewId(reviewId);
        if(review != null) {
            reviewService.updateReview(reviewDto, reviewId);
            return new ResponseEntity<>(new ResponseMessage(200, "강의 리뷰 수정 성공"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 강의 리뷰"), HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{reviewId}") // 리뷰 삭제
    public ResponseEntity<ResponseMessage> deleteReview(@RequestBody ReviewDto reviewDto, Principal principal) {


        return new ResponseEntity<>(new ResponseMessage(201, "강의 리뷰가 신고되었습니다."), HttpStatus.CREATED);
    }

    @PostMapping("/{reviewId}/reports") // 리뷰 신고
    public ResponseEntity<ResponseMessage> createReport(@RequestBody LectureDto lectureDto, Principal principal) {


        return new ResponseEntity<>(new ResponseMessage(201, "강의 리뷰가 신고되었습니다."), HttpStatus.CREATED);
    }

}
