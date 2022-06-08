package com.example.demo.review.repository;

import com.example.demo.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, CustomReviewRepository {
}
