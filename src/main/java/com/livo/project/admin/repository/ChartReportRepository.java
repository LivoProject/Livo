package com.livo.project.admin.repository;

import java.time.LocalDateTime;
import java.util.List;

public interface ChartReportRepository {

    List<Object[]> rawTopLectureStats(LocalDateTime from, LocalDateTime to, int limit);

    List<Object[]> rawMonthlySignups(LocalDateTime from, LocalDateTime toExclusive);

    List<Object[]> rawMonthlyRevenue(LocalDateTime from, LocalDateTime to);

    List<Object[]> rawInstructorOps(LocalDateTime from, LocalDateTime toExclusive, int limit);
}
