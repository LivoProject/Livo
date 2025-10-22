package com.livo.project.report.controller;

import com.livo.project.report.domain.Report;
import com.livo.project.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/lecture")
public class ReportController {

    private final ReportService reportService;

    // 민영 신고 등록: DB 저장!!
    @PostMapping("/content/{lectureId}/report")
    public String submitReport(@PathVariable int lectureId,
                               @RequestParam("reviewUId") int reviewUId,
                               @RequestParam("reportReason") String reportReason,
                               @RequestParam(value = "customReason", required = false) String customReason,
                               @AuthenticationPrincipal UserDetails userDetails) {

        String userEmail = userDetails.getUsername();

        if ("기타".equals(reportReason) && customReason != null && !customReason.trim().isEmpty()) {
            reportReason = customReason.trim();
        }

        Report report = new Report();
        report.setReviewUId(reviewUId);
        report.setReportReason(reportReason);
        report.setEmail(userEmail);

        reportService.saveReport(report);

        return "redirect:/lecture/content/" + lectureId + "?reported=success#review";
    }
}
