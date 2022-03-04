package com.example.demo.lecture;
import com.example.demo.dto.*;
import com.example.demo.hashtag.service.HashtagService;
import com.example.demo.lecture.dto.AllLecturesResponse;
import com.example.demo.lecture.dto.LectureDto;
import com.example.demo.lecture.dto.DetailLectureResponse;
import com.example.demo.lecture.dto.UrlCheckDto;
import com.example.demo.like.Like;
import com.example.demo.like.LikeService;
import com.example.demo.review.Review;
import com.example.demo.review.dto.ReviewPostDto;
import com.example.demo.reviewHashtag.ReviewHashtagService;
import com.example.demo.review.ReviewService;
import com.example.demo.user.UserDetailsServiceImpl;
import com.example.demo.user.User;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Api(tags = {"Lecture"})
@RestController
@RequiredArgsConstructor
@Transactional
@RequestMapping("/lectures")
public class LectureController {
    private final LectureService lectureService;
    private final ReviewService reviewService;
    private final UserDetailsServiceImpl userDetailsService;
    private final LikeService likeService;
//    @PersistenceContext
//    private final EntityManager em;

    // 전체 강의 글 조회 . 필터링 된 강의 글 조회
    @GetMapping("")
    public ResponseEntity<ResponseMessage> getLectures(@RequestParam(required = false) String keyword, @RequestParam(required = false) String category) {
        if(keyword == null && category == null) { // 모든 강의 조회
            List<AllLecturesResponse> lectures = lectureService.getLectures();
            return new ResponseEntity<>(ResponseMessage.withData(200, "모든 강의를 조회했습니다", lectures), HttpStatus.OK);
        }
        else { // 검색어별 조회 or 해시태그(카테고리)별 조회
            List<AllLecturesResponse> lectures = lectureService.getFilteredLectures(keyword, category);
            return new ResponseEntity<>(ResponseMessage.withData(200, "필터링 된 강의리뷰 조회", lectures), HttpStatus.OK);
        }
    }

    @GetMapping("/{lectureId}") // 강의글 상세 조회
    public ResponseEntity<ResponseMessage> getLecture(@PathVariable("lectureId") Long lectureId, Principal principal) {
        String email = principal.getName();
        User user = userDetailsService.findUserByEmail(email);
        Lecture lecture = lectureService.findById(lectureId);
        if(lecture != null) {// 강의정보가 있는 경우만
            DetailLectureResponse detailLectureResponse = lectureService.getLecture(lecture.getLectureId(), user.getUserId());
            return new ResponseEntity<>(ResponseMessage.withData(200, "강의를 조회했습니다", detailLectureResponse), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseMessage(404, "해당하는 강의가 없습니다"), HttpStatus.NOT_FOUND);
    }

    @PostMapping("/{lectureId}/reviews") // 강의에 들어가서 리뷰 다는 경우
    public ResponseEntity<ResponseMessage> createReview(@RequestBody ReviewPostDto reviewPostDto, @PathVariable("lectureId") Long lectureId, Principal principal) {
        String email = principal.getName();
        User user = userDetailsService.findUserByEmail(email);
        Lecture lecture = lectureService.findById(lectureId);
        List<String> hashtags = reviewPostDto.getHashtags();
        if(lecture!=null){ // 강의가 있는 경우
            Review review = reviewService.findByUserAndLecture(user, lecture);
            if(review == null) { // 리뷰 등록한 적 없는 경우
                review = new Review();
                review.setLectureReview(reviewPostDto, user, lecture);
                reviewService.saveReview(review);
                lectureService.manageHashtag(hashtags, review);
                return new ResponseEntity<>(new ResponseMessage(201, "강의 탭에서 리뷰 등록 성공"), HttpStatus.CREATED);
            }
            else
                return new ResponseEntity<>(new ResponseMessage(409, "리뷰 여러 번 업로드 불가"), HttpStatus.CONFLICT);
        }
        else
            return new ResponseEntity<>(new ResponseMessage(404, "존재하지 않는 강의"), HttpStatus.NOT_FOUND);
    }

    @PostMapping("/{lectureId}/likes") // 강의글 좋아요
    public ResponseEntity<ResponseMessage> createLike(@PathVariable("lectureId") Long lectureId, Principal principal) {
        // 현재로그인한 사용자 아이디 가져오기
        String email = principal.getName();
        User user = userDetailsService.findUserByEmail(email);
        Lecture lecture = lectureService.findById(lectureId);
        if(lecture!=null) { // 강의정보가 있는 경우
            Like existedLike = likeService.findLikeByLectureAndUser(lecture, user);
            if(existedLike!=null) { // 좋아요가 존재하는 경우
                if(existedLike.getLikeStatus()==1) { // 이미 눌려있는 경우
                    existedLike.changeLikeStatus(0);
                    return new ResponseEntity<>(new ResponseMessage(200, "좋아요 취소 성공"), HttpStatus.OK);
                }
                else {
                    existedLike.changeLikeStatus(1);
                    return new ResponseEntity<>(new ResponseMessage(200, "좋아요 재등록 성공"), HttpStatus.OK);
                }
//                int status = likeService.changeLikeStatus(existedLike, existedLike.getLikeStatus());
//                if(status==1)
//                    return new ResponseEntity<>(new ResponseMessage(200, "좋아요 재등록 성공"), HttpStatus.OK);
//                else
//                    return new ResponseEntity<>(new ResponseMessage(200, "좋아요 취소 성공"), HttpStatus.OK);
            }
            else {// 좋아요 처음 누른 경우
                Like like = new Like(lecture, user);
//                em.persist(like);
                likeService.saveLike(like);
                return new ResponseEntity<>(new ResponseMessage(201, "좋아요 등록 성공"), HttpStatus.CREATED);
            }
        }
        return new ResponseEntity<>(new ResponseMessage(404, "해당하는 강의가 없습니다"), HttpStatus.NOT_FOUND);
    }

    @PostMapping("") // 강의 등록
    public ResponseEntity<ResponseMessage> createLecture(@RequestBody LectureDto lectureDto, Principal principal) {
        // 현재로그인한 사용자 아이디 가져오기
        String email = principal.getName();
        User user = userDetailsService.findUserByEmail(email);

        // 여기까지는 lecture table에 들어가는 것
        String lectureUrl = lectureDto.getLectureUrl();
        String lectureTitle = lectureDto.getLectureTitle();
        String lecturer = lectureDto.getLecturer();
        String siteName = lectureDto.getSiteName();
        String thumbnailUrl = lectureDto.getThumbnailUrl();

        // review_hashTag 테이블에 들어가는 것
        List<String> hashtags = lectureDto.getHashtags();

        int rate = lectureDto.getRate().intValue();
        String commentTitle = lectureDto.getCommentTitle();
        String comment = lectureDto.getComment();
        // 강의 id 에 해당하는 내가 쓴 리뷰가 존재하는 경우

        Review review = new Review(rate, LocalDateTime.now(), commentTitle, comment);

        Lecture existedLecture = lectureService.findByUrl(lectureUrl); // url 이 있는 경우
        if(existedLecture == null) { // 강의가 없어서 새로 등록하는 경우
            Lecture lecture = new Lecture(lectureTitle, lecturer, siteName, lectureUrl, thumbnailUrl);
            lecture.setUser(user);
            lectureService.saveLecture(lecture);
            review.setLecture(lecture);
        }
        else {  // 강의가 이미 존재하는 경우
            if(existedLecture.getUser().getUserId() == user.getUserId())// 동일인물이 중복된 강의를 올리려는 경우
                return new ResponseEntity<>(new ResponseMessage(409, "동일한 강의리뷰 업로드 불가"), HttpStatus.CONFLICT);

            Review existedReview = reviewService.findByUserAndLecture(user, existedLecture);
            if(existedReview != null)   // 해당 유저가 이미 쓴 리뷰가 있다면
                return new ResponseEntity<>(new ResponseMessage(409, "리뷰 여러 번 업로드 불가"), HttpStatus.CONFLICT);
            review.setLecture(existedLecture);
        }
        review.setUser(user);
        reviewService.saveReview(review); // 리뷰 저장
        System.out.println("hashtags = " + hashtags);
        lectureService.manageHashtag(hashtags, review); // reviewHashtag에 등록 및 hashtag 관리
        return new ResponseEntity<>(new ResponseMessage(201, "강의 리뷰가 등록되었습니다."), HttpStatus.CREATED);
    }

    @PostMapping("/urls") // 중복링크 찾기
    public ResponseEntity<ResponseMessage> checkLectureUrl(@RequestBody UrlCheckDto urlCheckDto){
        String lectureUrl = urlCheckDto.getLectureUrl();
        Lecture lecture = lectureService.findByUrl(lectureUrl);
        if(lecture!=null)// 중복링크가 있으면
            return new ResponseEntity<>(ResponseMessage.withData(200, "중복된 링크가 존재합니다.", lecture), HttpStatus.OK);
        return new ResponseEntity<>(new ResponseMessage(200, "중복된 링크가 없습니다."), HttpStatus.OK);
    }
}