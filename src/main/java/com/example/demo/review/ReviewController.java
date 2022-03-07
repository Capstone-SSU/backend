package com.example.demo.review;
import com.example.demo.dto.ResponseMessage;
import com.example.demo.report.Report;
import com.example.demo.review.dto.ReviewPostDto;
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

@Api(tags = {"Review"})
@RestController
@RequiredArgsConstructor
@Transactional
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final UserDetailsServiceImpl userDetailsService;
    private final ReportService reportService;
    private final EntityManager em;

    @PatchMapping("/{reviewId}") // 리뷰 수정
    public ResponseEntity<ResponseMessage> updateReview(@PathVariable("reviewId") Long reviewId, @RequestBody ReviewPostDto reviewUpdateDto, Principal principal) {
        Review review = reviewService.findByReviewId(reviewId);
        if(review != null) {
            reviewService.updateReview(reviewUpdateDto, reviewId);
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
    public ResponseEntity<ResponseMessage> createReport(@PathVariable("reviewId") Long reviewId, @RequestBody HashMap<String, String> params, Principal principal) {
        String email = principal.getName();
        User user = userDetailsService.findUserByEmail(email);
        String content=params.get("reportContent");
        Review review = reviewService.findByReviewId(reviewId);
        if(review!=null) {// 리뷰가 있는 경우
            // 내가 해당 강의를 이미 신고한 경우
            Report existedReport = reportService.findByUserAndReview(user, review);
            if(existedReport!=null) {
                if (review.getReportCount() == 5) {// 5번 신고 된 경우 삭제
                    review.updateReviewStatus();
                    return new ResponseEntity<>(new ResponseMessage(200, "5번 누적되어 리뷰 삭제됨"), HttpStatus.OK);
                }
                int reportCnt = review.getReportCount();
                Report report = new Report(content, review);
                reportService.saveReport(report);
                review.updateReviewReportCount(reportCnt++);
                return new ResponseEntity<>(new ResponseMessage(201, "강의 리뷰가 신고되었습니다."), HttpStatus.CREATED);
            }
            return new ResponseEntity<>(new ResponseMessage(409, "이미 신고한 강의리뷰"), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 리뷰 번호"), HttpStatus.NOT_FOUND);
    }
}
