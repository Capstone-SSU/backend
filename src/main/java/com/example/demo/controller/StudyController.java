package com.example.demo.controller;

import com.example.demo.domain.StudyPost;
import com.example.demo.domain.User;
import com.example.demo.dto.ResponseMessage;
import com.example.demo.dto.StudyPostDTO;
import com.example.demo.security.UserDetailsServiceImpl;
import com.example.demo.service.StudyPostService;
import lombok.Getter;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RestController
public class StudyController {

    private final StudyPostService studyPostService;
    private final UserDetailsServiceImpl userDetailsService;
    @PersistenceContext
    private final EntityManager em;

    public StudyController(StudyPostService studyPostService, UserDetailsServiceImpl userDetailsService, EntityManager em) {
        this.studyPostService = studyPostService;
        this.userDetailsService = userDetailsService;
        this.em = em;
    }


    @PostMapping("/studies")
    public ResponseEntity<ResponseMessage> uploadStudyPost(@RequestBody StudyPostDTO post){
        //StudyPost 객체를 그대로 반환
        String title=post.getTitle();
        String content=post.getContent();
        String location=post.getLocation();
        Long writerId=post.getUserId();
        Integer recruitStatus=post.getRecruitStatus();
        String category=post.getCategory();
        Integer min=post.getMinReq();
        //maxReq는 채워져 있으면 받아옴

        StudyPost newPost = new StudyPost(title,content,category,location,recruitStatus,min);
        if(post.getMaxReq()!=null){
            newPost.setStudyMaxReq(post.getMaxReq());
        }

        User user=userDetailsService.findUserById(writerId);
//        System.out.println("USER: "+user);
        newPost.setUser(user); // 외래키로 연결된 User를 저장함 ->
//        em.persist(newPost);
        studyPostService.saveStudyPost(newPost);

        return new ResponseEntity<>(ResponseMessage.withData(200,"스터디글이 등록 되었습니다.",newPost), HttpStatus.OK);
    }

    @GetMapping("/studies/{id}")
    public ResponseEntity<ResponseMessage> test(@PathVariable Long id){
        StudyPost post=studyPostService.findStudyPostById(id);
        return new ResponseEntity<>(ResponseMessage.withData(200,"스터디글 찾음",post),HttpStatus.OK);
    }
}
