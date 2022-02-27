package com.example.demo.study.repository;

import com.example.demo.study.domain.StudyPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyPostRepository extends JpaRepository<StudyPost,Long>, CustomStudyPostRepository {

}
