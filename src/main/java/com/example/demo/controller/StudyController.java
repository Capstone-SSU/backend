package com.example.demo.controller;

import com.example.demo.domain.Report;
import com.example.demo.domain.StudyPost;
import com.example.demo.domain.User;
import com.example.demo.dto.ResponseMessage;
import com.example.demo.dto.StudyPostDTO;
import com.example.demo.security.UserDetailsServiceImpl;
import com.example.demo.service.ReportService;
import com.example.demo.service.StudyPostService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.security.Principal;
import java.util.HashMap;

@RestController
@AllArgsConstructor
@Transactional
public class StudyController {

    private final StudyPostService studyPostService;
    private final UserDetailsServiceImpl userDetailsService;
    private final ReportService reportService;
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

    @GetMapping("/studies/{studyId}") // 스터디 댓글 기능, 엔티티 생성하고 나면 이것도 연결하기
    public ResponseEntity<ResponseMessage> viewStudyPost(@PathVariable Long studyId){
        StudyPost post=studyPostService.findStudyPostById(studyId);
        //댓글데이터까지 함꼐 ResponseMessage에 넣어서 반환해주기
        if(post!=null&&post.getStudyStatus()==1){
            return new ResponseEntity<>(ResponseMessage.withData(200,"스터디글 찾음",post),HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseMessage(400,"존재하지 않는 스터디글 요청"),HttpStatus.OK);
    }

    @PatchMapping("/studies/{studyId}")
    public ResponseEntity<ResponseMessage> modifyPost(@PathVariable Long studyId, @RequestBody StudyPostDTO postDTO){
        //recruitStatus, repostCount, user, postId, createdDate, studyStatus 제외하고 update
        StudyPost post=studyPostService.modifyStudyPost(postDTO,studyId);
        if(post!=null){
            return new ResponseEntity<>(ResponseMessage.withData(200,"스터디글 수정 성공",post),HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new ResponseMessage(404,"잘못된 수정 요청"),HttpStatus.OK);
        }
    }

    @DeleteMapping("/studies/{studyId}")
    public ResponseEntity<ResponseMessage> deletePost(@PathVariable Long studyId){
        StudyPost post=studyPostService.findStudyPostById(studyId);
        if(post!=null){
            post.updateStudyStatus(0); // 0이면 삭제된 글
            studyPostService.saveStudyPost(post); //삭제한 정보를 반영 -> 근데 삭제된 글이면 User가 가지고 있는 글 보여줄 때도 status 0인 글 제외하고 보여줄 수 있겠지?
            return new ResponseEntity<>(new ResponseMessage(200,studyId+"번 글 삭제 성공"),HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new ResponseMessage(404,"잘못된 삭제 요청"),HttpStatus.OK);
        }
    }

    @PostMapping ("/studies/{studyId}/reports")
    public ResponseEntity<ResponseMessage> reportPost(@PathVariable Long studyId, @RequestBody HashMap<String, String> params ){
        String content=params.get("reportContent");

        StudyPost post=studyPostService.findStudyPostById(studyId);
        Report report=new Report(content,0);
        report.setStudyPost(post);
        em.persist(report);
        reportService.saveReport(report);

        Integer reportCount=post.getStudyReportCount();
        post.updateStudyReposrtCount(++reportCount);

        if(reportCount==5){
            post.updateStudyStatus(0); // 5번 신고된 글은 삭제 처리
            studyPostService.saveStudyPost(post);
            return new ResponseEntity<>(new ResponseMessage(200,studyId+"번 글은 신고가 5번 누적되어 삭제되었습니다."),HttpStatus.OK);
        }

        studyPostService.saveStudyPost(post); // 신고내역 update 후 저장
        return new ResponseEntity<>(new ResponseMessage(200,studyId+"번 글 신고 완료"),HttpStatus.OK);
    }


}
