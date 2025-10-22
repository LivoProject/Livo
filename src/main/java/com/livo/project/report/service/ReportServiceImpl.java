package com.livo.project.report.service;

import com.livo.project.report.domain.Report;
import com.livo.project.report.repository.ReportRepository;
import com.livo.project.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final ReviewRepository reviewRepository;
    @Override
    public Report saveReport(Report report) {
        return reportRepository.save(report);
    }

    @Override
    public void approveReport(int reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() ->new IllegalArgumentException("해당 신고가 존재 하지 않습니다."));
        int reviewId = report.getReviewUId();
        //관계끊기 (영속성 문제 방지)
        report.setReview(null);
        reportRepository.save(report);
        //리뷰삭제
        reviewRepository.deleteById(reviewId);
        //신고상태 변경
        report.setStatus(Report.Status.COMPLETED);
        reportRepository.save(report);
    }

    @Override
    public void rejectReport(int reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() ->new IllegalArgumentException("해당 신고가 존재 하지 않습니다."));
        report.setStatus(Report.Status.REJECT);
        reportRepository.save(report);
    }

    @Override
    public Page<Report> getReport(Pageable pageable) {
        return reportRepository.findAll(pageable);
    }
}
