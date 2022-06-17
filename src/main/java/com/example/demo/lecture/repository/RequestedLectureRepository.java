package com.example.demo.lecture.repository;

import com.example.demo.lecture.RequestedLecture;
import com.example.demo.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestedLectureRepository extends JpaRepository<RequestedLecture, Long> {
    Optional<RequestedLecture> findByLectureUrl(String url);
    List<RequestedLecture> findAllByUser(User user);
}