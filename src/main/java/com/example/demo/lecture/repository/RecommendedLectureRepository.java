package com.example.demo.lecture.repository;

import com.example.demo.lecture.RecommendedLecture;
import com.example.demo.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendedLectureRepository  extends JpaRepository<RecommendedLecture, Long> {
    List<RecommendedLecture> findByUser(User user);
}
