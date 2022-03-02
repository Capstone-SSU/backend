package com.example.demo.report;

import com.example.demo.report.Report;
import com.example.demo.review.Review;
import com.example.demo.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report,Long> {
    Optional<Report> findByUserAndReview(User user, Review review);
}
