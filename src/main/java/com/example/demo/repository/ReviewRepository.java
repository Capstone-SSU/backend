package com.example.demo.repository;

import com.example.demo.domain.Lecture;
import com.example.demo.domain.Review;
import com.example.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByUserAndLecture(User user, Lecture lecture);
    List<Review> findByLecture(Lecture lecture);
}
