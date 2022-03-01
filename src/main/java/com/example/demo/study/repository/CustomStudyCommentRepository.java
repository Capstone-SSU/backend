package com.example.demo.study.repository;

import com.example.demo.study.domain.StudyComment;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomStudyCommentRepository {
    List<StudyComment> findNestedComments(Long groupId);
}
