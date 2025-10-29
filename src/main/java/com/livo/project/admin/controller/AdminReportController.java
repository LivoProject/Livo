package com.livo.project.admin.controller;

import com.livo.project.admin.domain.dto.ReportResponse;
import com.livo.project.report.domain.Report;
import com.livo.project.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/report")
public class AdminReportController {
    private final ReportService reportService;

    @GetMapping("/list")
    @ResponseBody
    public Page<ReportResponse> getReportList(@RequestParam(defaultValue = "0")int page,
                                              @RequestParam(defaultValue = "10")int pageSize){
        Pageable pageable = PageRequest.of(page,pageSize, Sort.by(Sort.Direction.DESC,"reportTime"));
        return reportService.getReport(pageable);
    }
    @PostMapping("/approve/{reportId}")
    public ResponseEntity<?> approveReport(@PathVariable("reportId") int reportId){
        try{
            reportService.approveReport(reportId);
            return ResponseEntity.ok("신고 승인 완료(해당 리뷰 삭제됨)");
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("승인 실패: "+e.getMessage());
        }
    }

    @PostMapping("/reject/{reportId}")
    public ResponseEntity<?> rejectReport(@PathVariable("reportId") int reportId){
        try{
            reportService.rejectReport(reportId);
            return ResponseEntity.ok("신고 거부 완료");
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("거부 실패: "+e.getMessage());
        }
    }



}
