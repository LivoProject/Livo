package com.livo.project.admin.controller;

import com.livo.project.admin.domain.dto.InstructorOpsDto;
import com.livo.project.admin.domain.dto.MonthlyRevenueDto;
import com.livo.project.admin.domain.dto.MonthlySignupDto;
import com.livo.project.admin.domain.dto.TopLectureDto;
import com.livo.project.admin.service.ChartService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/chart/api")
public class ChartController {

    private final ChartService chartService;

    public ChartController(ChartService chartService) {
        this.chartService = chartService;
    }

    // Top Lectures
    @GetMapping(value = "/lectures/top", produces = "application/json")
    @ResponseBody
    public ResponseEntity<List<TopLectureDto>> topLectures(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "5") int limit) {

        List<TopLectureDto> body = chartService.topLectures(from, to, limit);
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, max-age=0")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .header(HttpHeaders.EXPIRES, "0")
                .body(body);
    }

    // Monthly Revenue
    @GetMapping(value = "/revenue/monthly", produces = "application/json")
    @ResponseBody
    public ResponseEntity<List<MonthlyRevenueDto>> monthlyRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        List<MonthlyRevenueDto> body = chartService.monthlyRevenue(from, to);
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, max-age=0")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .header(HttpHeaders.EXPIRES, "0")
                .body(body);
    }

    // Monthly Signups

    @GetMapping(value = "/signups/monthly", produces = "application/json")
    public ResponseEntity<List<MonthlySignupDto>> monthlySignups(
            @RequestParam(required = false) String from,   // "yyyy-MM"
            @RequestParam(required = false) String to      // "yyyy-MM"
    ) {
        // 기본값: 최근 12개월
        if (from == null || to == null) {
            java.time.YearMonth toYm = java.time.YearMonth.now();
            java.time.YearMonth fromYm = toYm.minusMonths(11);
            from = fromYm.toString();   // "YYYY-MM"
            to = toYm.toString();
        }

        // 형식 검증 (옵션)
        try {
            java.time.YearMonth.parse(from);
            java.time.YearMonth.parse(to);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        List<MonthlySignupDto> body = chartService.monthlySignups(from, to);
        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, max-age=0")
                .header(org.springframework.http.HttpHeaders.PRAGMA, "no-cache")
                .header(org.springframework.http.HttpHeaders.EXPIRES, "0")
                .body(body);
    }
    @GetMapping(value = "/instructors/ops", produces = "application/json")
    @ResponseBody
    public ResponseEntity<List<InstructorOpsDto>> instructorOps(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "5") int limit
    ) {
        var body = chartService.instructorOps(from, to, limit);
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, max-age=0")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .header(HttpHeaders.EXPIRES, "0")
                .body(body);
    }
}
