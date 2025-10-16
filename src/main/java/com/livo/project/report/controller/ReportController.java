package com.livo.project.report.controller;

import com.livo.project.report.domain.Report;
import com.livo.project.report.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/lecture")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // 민영 신고 등록: DB 저장!!
    @PostMapping("/content/{lectureId}/report")
    public String submitReport(@PathVariable int lectureId,
                               @RequestParam("reviewUId") int reviewUId,
                               @RequestParam("reportReason") String reportReason) {

        String userEmail = "test@livo.com"; //임시 유저야!! 나중에 로그인 연결해야해!

        Report report = new Report();
        report.setReviewUId(reviewUId);
        report.setReportReason(reportReason);
        report.setEmail(userEmail);

        reportService.saveReport(report);

        return "redirect:/lecture/content/" + lectureId;
    }
}
