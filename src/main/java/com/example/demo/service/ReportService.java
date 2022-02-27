package com.example.demo.service;

import com.example.demo.domain.Report;
import com.example.demo.repository.ReportRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class ReportService {
    private final ReportRepository reportRepository;

    public void saveReport(Report report){
        reportRepository.save(report);
    }


}
