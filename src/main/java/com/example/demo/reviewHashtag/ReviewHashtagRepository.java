package com.example.demo.reviewHashtag;

import com.example.demo.review.Review;
import com.example.demo.reviewHashtag.ReviewHashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewHashtagRepository extends JpaRepository<ReviewHashtag, Long> {
    List<ReviewHashtag> findByReview(Review review);

}
