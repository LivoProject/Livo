package com.livo.project.admin.service;

import com.livo.project.admin.domain.dto.MonthlyRevenueDto;
import com.livo.project.admin.domain.dto.MonthlySignupDto;
import com.livo.project.admin.domain.dto.TopLectureDto;

import java.time.LocalDate;
import java.util.List;

public interface ChartService {

    /** 강의별 예약/수강 현황 Top N */
    List<TopLectureDto> topLectures(LocalDate from, LocalDate to, int limit);

    /** 월별 매출/결제 현황 */
    List<MonthlyRevenueDto> monthlyRevenue(LocalDate from, LocalDate to);

    /** 월별 신규 가입자(예약 기반 추정) */
    List<MonthlySignupDto> monthlySignups();
}
