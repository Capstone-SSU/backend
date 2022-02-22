package com.example.demo.controller;
import com.example.demo.domain.Lecture;
import com.example.demo.domain.User;
import com.example.demo.dto.LectureDto;
import com.example.demo.dto.ResponseMessage;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.security.UserDetailsServiceImpl;
import com.example.demo.service.LectureService;
import com.example.demo.service.StudyPostService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Transactional
@RequestMapping("/lectures")
public class LectureController {
    private final LectureService lectureService;
    private final UserDetailsServiceImpl userDetailsService;
    private final EntityManager em;

//    @GetMapping("/lectures")
//    public String hi(){
//        return "hi";
//    }

    @PostMapping("")
    public ResponseEntity<ResponseMessage> createLecture(@RequestBody LectureDto lectureDto, Principal principal) {
        String email = principal.getName(); // 동작됨
        String lectureUrl = lectureDto.getLectureUrl();
        String lectureTitle = lectureDto.getLectureTitle();
        String lecturer = lectureDto.getLecturer();
        String siteName = lectureDto.getSiteName();
        String thumbnailUrl = lectureDto.getThumbnailUrl();
//                List<String> hashtags = lectureDto.getHashtags();
        // 여기까지는 lecture table에 들어가는 것

        // 현재로그인한 사용자 아이디 가져오기
        User user = userDetailsService.findUserByEmail(email);
        System.out.println("user = " + user);
        int rate = lectureDto.getRate().intValue();
        String commentTitle = lectureDto.getCommentTitle();
        String comment = lectureDto.getComment();
        Lecture lecture = new Lecture(lectureTitle, lecturer, siteName, lectureUrl, thumbnailUrl);
        lecture.setUser(user);
        em.persist(lecture);
        lectureService.saveLecture(lecture);
        return new ResponseEntity<>(new ResponseMessage(201, "강의 리뷰가 등록되었습니다."), HttpStatus.CREATED);
    }
}