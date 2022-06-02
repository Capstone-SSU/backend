package com.example.demo.report;

import com.example.demo.review.Review;
import com.example.demo.study.domain.StudyComment;
import com.example.demo.study.domain.StudyPost;
import com.example.demo.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report,Long> {
    Optional<Report> findByUserAndReview(User user, Review review);
    Optional<Report> findByUserAndStudyPost(User user, StudyPost post);
    Optional<Report> findByUserAndStudyComment(User user, StudyComment comment);
}
