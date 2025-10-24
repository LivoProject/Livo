package com.livo.project.admin.controller;

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
    public ChartController(ChartService chartService) { this.chartService = chartService; }

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
    @ResponseBody
    public ResponseEntity<List<MonthlySignupDto>> monthlySignups() {
        List<MonthlySignupDto> body = chartService.monthlySignups();
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, max-age=0")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .header(HttpHeaders.EXPIRES, "0")
                .body(body);
    }
}
