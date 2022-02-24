package com.example.demo.controller;
import com.example.demo.domain.*;
import com.example.demo.dto.LectureDto;
import com.example.demo.dto.ResponseMessage;
import com.example.demo.security.UserDetailsServiceImpl;
import com.example.demo.service.HashtagService;
import com.example.demo.service.LectureService;
import com.example.demo.service.ReviewHashtagService;
import com.example.demo.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

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

        Lecture lecture = new Lecture(lectureTitle, lecturer, siteName, lectureUrl, thumbnailUrl);
        lecture.setUser(user);
//        em.persist(lecture);
        long lectureId = lectureService.saveLecture(lecture);

        Review review = new Review(rate, LocalDateTime.now(), commentTitle, comment);
        review.setLecture(lecture);
        review.setUser(user);
//        em.persist(review);
        long reviewId = reviewService.saveReview(review);

        for (int i = 0; i < hashtags.size(); i++) {
            Hashtag existedHashtag = hashtagService.findByName(hashtags.get(i));
            ReviewHashtag reviewHashtag = new ReviewHashtag();
            if(existedHashtag!=null) { // 이미 들어간 해시태그라면 id 받아오기
                reviewHashtag.setHashtag(existedHashtag);
            }
            else { // 없는 해시태그라면 해시태그를 생성하고 나서 reviewHashtag 에 넣기
                Hashtag hashtag = new Hashtag(hashtags.get(i));
                long hashtagId = hashtagService.saveHashtag(hashtag);
                reviewHashtag.setHashtag(hashtag);
            }
            reviewHashtag.setReview(review);
            reviewHashtagService.saveReviewHashtag(reviewHashtag);
        }
        return new ResponseEntity<>(new ResponseMessage(201, "강의 리뷰가 등록되었습니다."), HttpStatus.CREATED);
    }

    @PostMapping("/urls") // 중복링크 찾기
    public ResponseEntity<ResponseMessage> checkLectureUrl(@RequestBody String lectureUrl){
        Lecture lecture = lectureService.findByUrl(lectureUrl);
        System.out.println("lecture = " + lecture); // 제목, 강의자, 사이트명, 이미지 url
        if(lecture!=null)// 중복링크가 없으면
            return new ResponseEntity<>(new ResponseMessage(200, "중복된 링크가 없습니다."), HttpStatus.OK);
        return new ResponseEntity<>(ResponseMessage.withData(200, "중복된 링크가 존재합니다.", lecture), HttpStatus.OK);
    }

}