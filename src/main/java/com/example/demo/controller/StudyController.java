package com.example.demo.controller;

import com.example.demo.domain.StudyPost;
import com.example.demo.domain.User;
import com.example.demo.dto.ResponseMessage;
import com.example.demo.dto.StudyPostDTO;
import com.example.demo.security.UserDetailsServiceImpl;
import com.example.demo.service.StudyPostService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.security.Principal;

@RestController
@AllArgsConstructor
@Transactional
public class StudyController {

    private final StudyPostService studyPostService;
    private final UserDetailsServiceImpl userDetailsService;
    @PersistenceContext
    private final EntityManager em;


    @PostMapping("/studies")
    public ResponseEntity<ResponseMessage> uploadStudyPost(@RequestBody StudyPostDTO postDto, Principal principal){
        //StudyPost 객체를 그대로 반환
        StudyPost newPost = new StudyPost(postDto);

        String email=principal.getName();
        User user=userDetailsService.findUserByEmail(email);
        newPost.setUser(user); // 외래키로 연결된 User를 저장함 ->
        em.persist(newPost);
        studyPostService.saveStudyPost(newPost);

        return new ResponseEntity<>(ResponseMessage.withData(200,"스터디글이 등록 되었습니다.",newPost), HttpStatus.OK);
    }

    @GetMapping("/studies/{id}") // 스터디 댓글 기능, 엔티티 생성하고 나면 이것도 연결하기
    public ResponseEntity<ResponseMessage> viewStudyPost(@PathVariable Long id){
        StudyPost post=studyPostService.findStudyPostById(id);
        //댓글데이터까지 함꼐 ResponseMessage에 넣어서 반환해주기
        if(post!=null){
            return new ResponseEntity<>(ResponseMessage.withData(200,"스터디글 찾음",post),HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseMessage(400,"존재하지 않는 스터디글 요청"),HttpStatus.OK);
    }

    @PatchMapping("/studies/{id}")
    public ResponseEntity<ResponseMessage> modifyPost(@PathVariable Long id, @RequestBody StudyPostDTO postDTO){
        //스터디글에서 수정 가능한것: 제목, 내용, 카테고리, 위치, 최소인원, 최대인원 - >DTO에서 userId만 null로 받아오고 바꾸면 될듯?
        //그러면 파라미터로 넘어온 id를 통해서 Post찾고, update 쿼리문을 써서 바꿔주기
        //필요 부분만 업데이트 해주어야 하므로 update 쿼리 작성 ->  recruiteStatus, repostCount, user, postId, createdDate, studyStatus 제외하고 update
        StudyPost post=studyPostService.modifyStudyPost(postDTO,id);
        if(post!=null){
            return new ResponseEntity<>(ResponseMessage.withData(200,"스터디글 수정 성공",post),HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new ResponseMessage(400,"잘못된 수정 요청"),HttpStatus.OK);
        }
    }
}
