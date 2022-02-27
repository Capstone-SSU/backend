package com.example.demo.study.service;

import com.example.demo.study.domain.StudyComment;
import com.example.demo.study.domain.StudyPost;
import com.example.demo.study.dto.StudyCommentResponse;
import com.example.demo.study.repository.StudyCommentRepository;
import com.example.demo.user.UserDetailsServiceImpl;
import com.example.demo.user.dto.SimpleUserDto;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@AllArgsConstructor
public class StudyCommentService {
    //댓글 넘겨줄 때 primary id 오름차순으로 정렬해서 넘겨줌 (필요시 그룹 id 별로 먼저 정렬, 그리고 내부에서 추가정렬)
    private final StudyCommentRepository studyCommentRepository;
    private final UserDetailsServiceImpl userDetailsService;

    public Long saveStudyComment(StudyComment studyComment){
        studyCommentRepository.save(studyComment);
        return studyComment.getStudyCommentId();
    }

    public StudyComment findStudyCommentById(Long commentId){
        Optional<StudyComment> comment = studyCommentRepository.findById(commentId);
        if(comment.isPresent()){
            StudyComment studyComment=comment.get();
            if(studyComment.getCommentStatus()==0){
                return null;
            }else{
                return studyComment;
            }
        }else{
            return null;
        }
    }

    public StudyComment modifyStudyComment(String content, Long commentId){
        StudyComment comment=findStudyCommentById(commentId);
        if(comment==null){
            return null;
        }
        comment.updateCommentContent(content);
        saveStudyComment(comment);
        return comment;
    }

    public List<StudyComment> findAllParentCommentsOnPosts(StudyPost studyPost){ //삭제되지 않은 댓글들만 return
        List<StudyComment> comments=studyCommentRepository.findAllByStudyPost(studyPost);
        Iterator<StudyComment> itr=comments.iterator();
        while(itr.hasNext()){
            StudyComment comment=itr.next();
            if(comment.getCommentStatus()==0||comment.getCommentClass()==1){ //자식댓글이거나 삭제된 댓글이면 제거
                itr.remove();
            }
        }
        return comments;
    }

    //한 댓글의 대댓글들 다 찾아주는 메소드 필요
    public List<StudyCommentResponse> getAllNestedCommentResponses(StudyComment studyComment,Long loginId, Long postUserId){
        Long groupId=studyComment.getStudyCommentId();
        List<StudyComment> allNestedComments=studyCommentRepository.findNestedComments(groupId);

        //commentId가 작은 순으로 정렬 (작을수록 먼저 달린 댓글)
        //각 대댓글들을 다 studyCommentResponse 객체에 담아주고, 그 내부의 nested는 다 null 로 세팅한다.
        return getAllCommentResponses(allNestedComments,loginId,postUserId);
    }

    public List<StudyCommentResponse> getAllCommentResponses(List<StudyComment> comments,Long loginId,Long postUserId){
        List<StudyCommentResponse> responseComments=new ArrayList<>();
        System.out.println(", loginId = " + loginId + ", postUserId = " + postUserId);

        for(StudyComment comment:comments) {
            StudyCommentResponse commentResponse = new StudyCommentResponse();
            SimpleUserDto commentUser = userDetailsService.getSimpleUserDto(comment.getUser());
            commentResponse.setUser(commentUser);
            commentResponse.setIsThisCommentWriterPostWriter(Objects.equals(postUserId, commentUser.getUserId()));
            commentResponse.setIsThisUserCommentWriter(Objects.equals(commentUser.getUserId(), loginId));
            //각 댓글들에 대한 작성자 정보 세팅 완료

            BeanUtils.copyProperties(comment, commentResponse); //댓글의 핵심 부분들만 복사

            //해당 comment 의 classId를 확인, 0이면 nestedComments 를 찾아주고 1이면 null 로 세팅
            commentResponse.setNestedComments(comment.getCommentClass() == 0 ? getAllNestedCommentResponses(comment, loginId, postUserId) : null);
            responseComments.add(commentResponse);
        }

        return responseComments;
    }


}
