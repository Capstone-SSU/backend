package com.example.demo.lectureHashtag;

import com.example.demo.lecture.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LectureHashtagRepository extends JpaRepository<LectureHashtag, Long> {
    List<LectureHashtag> findByLecture(Lecture lecture);
}
