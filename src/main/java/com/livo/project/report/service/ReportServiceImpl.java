package com.livo.project.report.service;

import com.livo.project.report.domain.Report;
import com.livo.project.report.repository.ReportRepository;
import org.springframework.stereotype.Service;

@Service
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    public ReportServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public Report saveReport(Report report) {
        return reportRepository.save(report);
    }
}
