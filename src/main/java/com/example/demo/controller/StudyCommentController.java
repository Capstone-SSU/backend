package com.example.demo.controller;

import com.example.demo.domain.StudyComment;
import com.example.demo.domain.StudyPost;
import com.example.demo.domain.User;
import com.example.demo.dto.ResponseMessage;
import com.example.demo.dto.StudyCommentDto;
import com.example.demo.security.UserDetailsServiceImpl;
import com.example.demo.service.StudyCommentService;
import com.example.demo.service.StudyPostService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.security.Principal;
import java.time.LocalDateTime;

@RestController
@AllArgsConstructor
@Transactional
public class StudyCommentController {
    private final StudyCommentService studyCommentService;
    private final UserDetailsServiceImpl userDetailsService;
    private final StudyPostService studyPostService;

    @PostMapping("/studies/{studyId}/comments")
    public ResponseEntity<ResponseMessage> addStudyComment(@PathVariable Long studyId, @RequestBody StudyCommentDto commentDto, Principal principal){
        String email=principal.getName();
        User user=userDetailsService.findUserByEmail(email);

        Long parentId=commentDto.getCommentParentId(); // 0이면 부모의 댓글이 온 것, 그 외면 얘의 부모 댓글이 온 것
        Integer classId=commentDto.getCommentClass(); // 0이면 부모댓글, 1이면 자식댓글
        String content=commentDto.getCommentContent();
        StudyPost studyPost=studyPostService.findStudyPostById(studyId);

        StudyComment comment=new StudyComment(content,classId);
        comment.setStudyPost(studyPost);
        comment.setUser(user);
        Long generatedId=studyCommentService.saveStudyComment(comment);

        comment.updateGroupId(parentId==0?generatedId:parentId); // parentId==0이면, 방금 등록한 애가 원댓글 -> 걔의 id가 그대로 groupId / 0이 아니면? parentId값 찾아서 등록

        studyCommentService.saveStudyComment(comment);
        return new ResponseEntity<>(ResponseMessage.withData(201,"스터디 댓글이 등록 되었습니다.",comment), HttpStatus.OK);

    }
}
