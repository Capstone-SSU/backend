package com.example.demo.report;

import com.example.demo.report.Report;
import com.example.demo.report.ReportRepository;
import com.example.demo.review.Review;
import com.example.demo.user.User;
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

}
