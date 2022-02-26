package com.example.demo.repository;

import com.example.demo.domain.StudyComment;
import com.example.demo.domain.StudyPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyCommentRepository extends JpaRepository<StudyComment,Long> {
    List<StudyComment> findAllByStudyPost(StudyPost post);
}
