package com.example.demo.review;
import com.example.demo.dto.ResponseMessage;
import com.example.demo.lecture.Lecture;
import com.example.demo.lecture.LectureService;
import com.example.demo.lecture.dto.LectureDto;
import com.example.demo.report.Report;
import com.example.demo.review.dto.ReviewDto;
import com.example.demo.review.dto.ReviewPostDto;
import com.example.demo.user.UserDetailsServiceImpl;
import com.example.demo.report.ReportService;
import com.example.demo.user.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Api(tags = {"Review"})
@RestController
@RequiredArgsConstructor
@Transactional
@RequestMapping("/reviews")
public class ReviewController {
    private final LectureService lectureService;
    private final ReviewService reviewService;
    private final UserDetailsServiceImpl userDetailsService;
    private final ReportService reportService;

    @ApiOperation(value="리뷰 등록")
    @ApiResponses({
            @ApiResponse(code = 201, message = "API 정상 작동 (리뷰 등록)"),
            @ApiResponse(code = 404, message = "존재하지 않는 유저 / 해당하는 강의 없음"),
            @ApiResponse(code = 409, message = "리뷰 여러 번 업로드 불가 / 동일한 강의글 업로드 불가"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @PostMapping("") // 리뷰 등록
    public ResponseEntity<ResponseMessage> createReview(@RequestBody ReviewDto reviewDto, Principal principal) {
        // 현재 로그인한 사용자 아이디 가져오기
        String email = principal.getName();
        User user = userDetailsService.findUserByEmail(email);
        if(user == null)
            return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 유저"), HttpStatus.NOT_FOUND);

        String lectureUrl = reviewDto.getLectureUrl();
        Lecture existedLecture = lectureService.findByUrl(lectureUrl); // url 이 있는 경우
        if(existedLecture == null) { // 강의가 없어서 새로 등록하는 경우 -> 링크 확인 버튼 눌렀을 때 없는 경우면 강의 등록 요청하도록
            return new ResponseEntity<>(new ResponseMessage(404, "해당하는 강의가 없음"), HttpStatus.NOT_FOUND);
        }

        // 강의가 이미 존재하는 경우
        Review existedReview = reviewService.findByUserAndLecture(user, existedLecture);
        if(existedReview != null)   // 해당 유저가 이미 쓴 리뷰가 있다면
            return new ResponseEntity<>(new ResponseMessage(409, "리뷰 여러 번 업로드 불가"), HttpStatus.CONFLICT);

        ReviewPostDto reviewPostDto = new ReviewPostDto();
        BeanUtils.copyProperties(reviewDto, reviewPostDto, lectureUrl);
        Review review = new Review();
        review.setLectureReview(reviewPostDto, user, existedLecture);
        reviewService.saveReview(review); // 리뷰 저장
        if(user.getReviewWriteStatus() == false) // 리뷰안썼다고 되어있으면 상태 변경
            user.updateReviewStatus();
        lectureService.setAvgRate(existedLecture, review.getRate()); // 특정 강의의 평점 업뎃
        return new ResponseEntity<>(new ResponseMessage(201, "강의 리뷰가 등록되었습니다.", existedLecture), HttpStatus.CREATED);
    }

    @PatchMapping("/{reviewId}") // 리뷰 수정
    public ResponseEntity<ResponseMessage> updateReview(@PathVariable("reviewId") Long reviewId, @RequestBody ReviewPostDto reviewUpdateDto) {
        Review review = reviewService.findByReviewId(reviewId);
        if(review != null) {
            reviewService.updateReview(reviewUpdateDto, review);
            return new ResponseEntity<>(new ResponseMessage(200, "강의 리뷰 수정 성공"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 강의 리뷰"), HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{reviewId}") // 리뷰 삭제
    public ResponseEntity<ResponseMessage> deleteReview(@PathVariable("reviewId") Long reviewId, Principal principal) {
        String email = principal.getName();
        User user = userDetailsService.findUserByEmail(email);
        Review review = reviewService.findByReviewId(reviewId);
        if(review != null) {
            reviewService.deleteReview(reviewId);
            List<Review> reviews = reviewService.findAllReviewsByUser(user);
            if(reviews.size() == 0) { // 삭제하고 나서 리뷰가 더이상 없는 경우 writeStatus 바꿔주기
                user.updateReviewStatus();
            }
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
                review.updateReviewReportCount(++reportCnt);
                return new ResponseEntity<>(new ResponseMessage(201, "강의 리뷰가 신고되었습니다."), HttpStatus.CREATED);
            }
            return new ResponseEntity<>(new ResponseMessage(409, "이미 신고한 강의리뷰"), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 리뷰 번호"), HttpStatus.NOT_FOUND);
    }
}
