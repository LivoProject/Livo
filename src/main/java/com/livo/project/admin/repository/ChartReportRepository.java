package com.livo.project.admin.repository;

import java.time.LocalDateTime;
import java.util.List;

public interface ChartReportRepository {

    List<Object[]> rawTopLectureStats(LocalDateTime from, LocalDateTime to, int limit);

    List<Object[]> rawMonthlySignups();

    List<Object[]> rawMonthlyRevenue(LocalDateTime from, LocalDateTime to);
}
