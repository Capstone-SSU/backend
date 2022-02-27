package com.example.demo.study.service;

import com.example.demo.study.domain.StudyComment;
import com.example.demo.study.domain.StudyPost;
import com.example.demo.study.repository.StudyCommentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class StudyCommentService {
    //댓글 넘겨줄 때 primary id 오름차순으로 정렬해서 넘겨줌 (필요시 그룹 id 별로 먼저 정렬, 그리고 내부에서 추가정렬)
    private final StudyCommentRepository studyCommentRepository;

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

    public List<StudyComment> findAllCommentsOnPosts(StudyPost studyPost){ //삭제되지 않은 댓글들만 return
        List<StudyComment> comments=studyCommentRepository.findAllByStudyPost(studyPost);
        Iterator<StudyComment> itr=comments.iterator();
        while(itr.hasNext()){
            StudyComment comment=itr.next();
            if(comment.getCommentStatus()==0){
                itr.remove();
            }
        }
        return comments;
    }

}
