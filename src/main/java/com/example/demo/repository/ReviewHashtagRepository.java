package com.example.demo.repository;

import com.example.demo.domain.Lecture;
import com.example.demo.domain.Review;
import com.example.demo.domain.ReviewHashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewHashtagRepository extends JpaRepository<ReviewHashtag, Long> {
    List<ReviewHashtag> findByReview(Review review);

}
