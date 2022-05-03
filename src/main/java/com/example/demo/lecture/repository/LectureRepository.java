package com.example.demo.lecture.repository;

import com.example.demo.lecture.Lecture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
// JPA criteria API 를 기반으로 Specification을 허용하는 인터페이스
public interface LectureRepository extends JpaRepository<Lecture, Long>, JpaSpecificationExecutor<Lecture> {
    Optional<Lecture> findBylectureUrl(String lectureUrl);
//    Page<Lecture> findAll(Pageable pageable);
    // findAll()에 Pageable 인터페이스로 파라미터를 넘기면 페이징 사용 가능

    Page<Lecture> findAll(@Nullable Specification<Lecture> spec, Pageable pageable);
}
