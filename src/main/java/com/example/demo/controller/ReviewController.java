package com.example.demo.controller;

import com.example.demo.domain.Lecture;
import com.example.demo.domain.User;
import com.example.demo.dto.LectureDto;
import com.example.demo.dto.ResponseMessage;
import com.example.demo.security.UserDetailsServiceImpl;
import com.example.demo.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import java.security.Principal;

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
}
