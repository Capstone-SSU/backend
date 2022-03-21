package com.example.demo.lecture.repository;

import com.example.demo.lecture.Lecture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long>{
    Optional<Lecture> findBylectureUrl(String lectureUrl);
    Page<Lecture> findAll(Pageable pageable);
    // findAll()에 Pageable 인터페이스로 파라미터를 넘기면 페이징 사용 가능
}
