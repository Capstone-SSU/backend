package com.example.demo.lecture.repository;

import com.example.demo.lecture.Lecture;
import com.example.demo.lecture.RequestedLecture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RequestedLectureRepository extends JpaRepository<RequestedLecture, Long> {
    Optional<RequestedLecture> findByLectureUrl(String url);
}