package com.livo.project.report.service;

import com.livo.project.report.domain.Report;
import com.livo.project.report.repository.ReportRepository;
import com.livo.project.review.domain.Review;
import com.livo.project.review.repository.ReviewRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final ReviewRepository reviewRepository;



    @Transactional
    @Override
    public void saveReport(int lectureId, int reviewUId, String reportReason, String customReason, String userEmail) {
        if ("기타".equals(reportReason) && customReason != null && !customReason.trim().isEmpty()) {
            reportReason = customReason.trim();
        }
        Review review = reviewRepository.findById(reviewUId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰가 존재하지 않습니다."));

        Report report = new Report();
        report.setReview(review);
        report.setReportReason(reportReason);
        report.setEmail(userEmail);
        reportRepository.save(report);
    }

    @Transactional
    @Override
    public void approveReport(int reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() ->new IllegalArgumentException("해당 신고가 존재 하지 않습니다."));

        Review review = report.getReview();
        if(review != null){
            review.setBlocked(true);
            reviewRepository.save(review);
        }
        reportRepository.save(report);
        report.setStatus(Report.Status.COMPLETED);
    }
    @Transactional
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

    @Override
    public List<Report> getNotApprovedReport() {
        return reportRepository.findTop5ByStatusOrderByReportTimeAsc(Report.Status.PROCESSING);
    }
}
