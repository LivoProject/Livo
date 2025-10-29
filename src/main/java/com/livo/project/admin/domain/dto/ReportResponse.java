package com.livo.project.admin.domain.dto;

import com.livo.project.report.domain.Report;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

@Data
public class ReportResponse {
    private int reportId;
    private String reportReason;
    private String status;
    private String email;
    private String reportTime;
    private String reviewContent;

    public ReportResponse(Report report) {
        this.reportId = report.getReportId();
        this.reportReason = report.getReportReason();
        this.status = report.getStatus().name();
        this.email = report.getEmail();
        this.reportTime = formatDateTime(report.getReportTime());
        this.reviewContent = report.getReview() != null ? report.getReview().getReviewContent() : "-";
    }
    private String formatDateTime(Date date) {
        if (date == null) return "-";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        return sdf.format(date);
    }
}
