package com.example.demo.study.repository;

import com.example.demo.study.domain.StudyComment;
import com.example.demo.study.domain.StudyPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyCommentRepository extends JpaRepository<StudyComment,Long> {
    List<StudyComment> findAllByStudyPost(StudyPost post);
}
