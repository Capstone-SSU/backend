package com.example.demo.report;

import com.example.demo.review.Review;
import com.example.demo.study.domain.StudyComment;
import com.example.demo.study.domain.StudyPost;
import com.example.demo.user.domain.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class ReportService {
    private final ReportRepository reportRepository;

    public void saveReport(Report report){
        reportRepository.save(report);
    }

    public Report findByUserAndReview(User user, Review review){
        Optional<Report> report = reportRepository.findByUserAndReview(user, review);
        return report.orElse(null);
    }

    public Report findByUserAndStudyPost(User user, StudyPost post){
        Optional<Report> foundReport = reportRepository.findByUserAndStudyPost(user, post);
        return foundReport.orElse(null);
    }

    public Report findByUserAndStudyComment(User user, StudyComment comment){
        Optional<Report> report = reportRepository.findByUserAndStudyComment(user, comment);
        return report.orElse(null);
    }

}
