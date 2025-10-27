package com.livo.project.admin.service;

import com.livo.project.admin.domain.dto.MonthlyRevenueDto;
import com.livo.project.admin.domain.dto.MonthlySignupDto;
import com.livo.project.admin.domain.dto.TopLectureDto;
import com.livo.project.admin.domain.dto.InstructorOpsDto;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface ChartService {

    /** 강의별 예약/수강 현황 Top N */
    List<TopLectureDto> topLectures(LocalDate from, LocalDate to, int limit);

    /** 월별 매출/결제 현황 */
    List<MonthlyRevenueDto> monthlyRevenue(LocalDate from, LocalDate to);

    /** 월별 신규 가입자 (yyyy-MM ~ yyyy-MM) */
    List<MonthlySignupDto> monthlySignups(String fromYm, String toYm);

    /** 강사별 운영 현황 */
    List<InstructorOpsDto> instructorOps(LocalDate from, LocalDate to, int limit);

    // 선택: 편의 오버로드 (최근 12개월)
    default List<MonthlySignupDto> monthlySignupsLast12M() {
        YearMonth to = YearMonth.now();
        YearMonth from = to.minusMonths(11);
        return monthlySignups(from.toString(), to.toString()); // "YYYY-MM"
    }
}
