package com.example.demo.review;

import com.example.demo.lecture.dto.LectureDto;
import com.example.demo.dto.ResponseMessage;
import com.example.demo.review.dto.UpdateReviewDto;
import com.example.demo.user.UserDetailsServiceImpl;
import com.example.demo.report.ReportService;
import com.example.demo.user.User;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import java.security.Principal;
import java.util.HashMap;

@Api(tags = { "Review"})
@RestController
@RequiredArgsConstructor
@Transactional
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final UserDetailsServiceImpl userDetailsService;
    private final ReportService reportService;
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
    public ResponseEntity<ResponseMessage> updateReview(@PathVariable("reviewId") Long reviewId, @RequestBody UpdateReviewDto updateReviewDto, Principal principal) {
        Review review = reviewService.findByReviewId(reviewId);
        if(review != null) {
            reviewService.updateReview(updateReviewDto, reviewId);
            return new ResponseEntity<>(new ResponseMessage(200, "강의 리뷰 수정 성공"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 강의 리뷰"), HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{reviewId}") // 리뷰 삭제
    public ResponseEntity<ResponseMessage> deleteReview(@PathVariable("reviewId") Long reviewId, Principal principal) {
        Review review = reviewService.findByReviewId(reviewId);
        if(review != null) {
            reviewService.deleteReview(reviewId);
            return new ResponseEntity<>(new ResponseMessage(200, "강의 리뷰 삭제 성공"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseMessage(200, "존재하지 않는 강의 리뷰"), HttpStatus.NOT_FOUND);
    }

    @PostMapping("/{reviewId}/reports") // 리뷰 신고
    public ResponseEntity<ResponseMessage> createReport(@PathVariable("reviewId") Long reviewId, @RequestBody HashMap<String, String> params) {
        String content=params.get("reportContent");
        Review review = reviewService.findByReviewId(reviewId);
//        if(review.)
//        Report report = new Report(content, review);
//        reportService.saveReport(report);

        return new ResponseEntity<>(new ResponseMessage(201, "강의 리뷰가 신고되었습니다."), HttpStatus.CREATED);
    }

}
