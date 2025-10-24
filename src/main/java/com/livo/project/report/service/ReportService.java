package com.livo.project.report.service;

import com.livo.project.report.domain.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReportService {

    //신고 등록 (민영은 등록만 하면 될 것 같음!)
    void saveReport(int lectureId, int reviewUId, String reportReason, String customReason, String userEmail);

    void approveReport(int reportId);

    void rejectReport(int reportId);

    Page<Report> getReport(Pageable pageable);

    List<Report> getNotApprovedReport();
}
