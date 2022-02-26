package com.example.demo.controller;
import com.example.demo.domain.*;
import com.example.demo.dto.LectureDto;
import com.example.demo.dto.ResponseMessage;
import com.example.demo.dto.UrlCheckDto;
import com.example.demo.security.UserDetailsServiceImpl;
import com.example.demo.service.HashtagService;
import com.example.demo.service.LectureService;
import com.example.demo.service.ReviewHashtagService;
import com.example.demo.service.ReviewService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Api(tags = { "Lecture"})
@RestController
@RequiredArgsConstructor
@Transactional
@RequestMapping("/lectures")
public class LectureController {
    private final LectureService lectureService;
    private final ReviewService reviewService;
    private final UserDetailsServiceImpl userDetailsService;
    private final HashtagService hashtagService;
    private final ReviewHashtagService reviewHashtagService;
    private final EntityManager em;

    @GetMapping("")
    public ResponseEntity<ResponseMessage> getAllLectures() {
        List<Lecture> lectures= lectureService.findAllLectures();
        System.out.println("lectures = " + lectures);
        return new ResponseEntity<>(ResponseMessage.withData(200, "강의를 조회했습니다", lectures), HttpStatus.OK);
    }

    @GetMapping("/{lectureId}")
    public ResponseEntity<ResponseMessage> getLecture(@PathVariable("lectureId") Long lectureId) {
        Lecture lecture = lectureService.findById(lectureId);
        if(lecture!=null)
            return new ResponseEntity<>(ResponseMessage.withData(200, "강의를 조회했습니다", lecture), HttpStatus.OK);
        return new ResponseEntity<>(new ResponseMessage(404, "해당하는 강의가 없습니다"), HttpStatus.NOT_FOUND);
    }

    @PostMapping("")
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
        Review review = new Review(rate, LocalDateTime.now(), commentTitle, comment);

        Lecture existedLecture = lectureService.findByUrl(lectureUrl);
        if(existedLecture == null) { // 강의가 없어서 새로 등록하는 경우
            Lecture lecture = new Lecture(lectureTitle, lecturer, siteName, lectureUrl, thumbnailUrl);
            lecture.setUser(user);
            lectureService.saveLecture(lecture);
            review.setLecture(lecture);
        }
        else  // 강의가 이미 존재하는 경우
            review.setLecture(existedLecture);
        review.setUser(user);
        reviewService.saveReview(review); // 리뷰 저장

        for (int i = 0; i < hashtags.size(); i++) {
            Hashtag existedHashtag = hashtagService.findByName(hashtags.get(i));
            ReviewHashtag reviewHashtag = new ReviewHashtag();
            if(existedHashtag!=null) { // 이미 들어간 해시태그라면 id 받아오기
                reviewHashtag.setHashtag(existedHashtag);
            }
            else { // 없는 해시태그라면 해시태그를 생성하고 나서 reviewHashtag 에 넣기
                Hashtag hashtag = new Hashtag(hashtags.get(i));
                hashtagService.saveHashtag(hashtag);
                reviewHashtag.setHashtag(hashtag);
            }
            reviewHashtag.setReview(review);
            reviewHashtagService.saveReviewHashtag(reviewHashtag);
        }
        return new ResponseEntity<>(new ResponseMessage(201, "강의 리뷰가 등록되었습니다."), HttpStatus.CREATED);
    }

    @PostMapping("/urls") // 중복링크 찾기
    public ResponseEntity<ResponseMessage> checkLectureUrl(@RequestBody UrlCheckDto urlCheckDto){
        String lectureUrl = urlCheckDto.getLectureUrl();
        Lecture lecture = lectureService.findByUrl(lectureUrl);
//        System.out.println("lecture = " + lecture); // 제목, 강의자, 사이트명, 이미지 url
        if(lecture!=null)// 중복링크가 있으면
            return new ResponseEntity<>(ResponseMessage.withData(200, "중복된 링크가 존재합니다.", lecture), HttpStatus.OK);
        return new ResponseEntity<>(new ResponseMessage(200, "중복된 링크가 없습니다."), HttpStatus.OK);
    }


}