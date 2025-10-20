package com.livo.project.report.service;

import com.livo.project.report.domain.Report;
import com.livo.project.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    @Override
    public Report saveReport(Report report) {
        return reportRepository.save(report);
    }
}
