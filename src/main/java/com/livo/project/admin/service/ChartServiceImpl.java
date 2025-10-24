package com.livo.project.admin.service;

import com.livo.project.admin.domain.dto.MonthlyRevenueDto;
import com.livo.project.admin.domain.dto.MonthlySignupDto;
import com.livo.project.admin.domain.dto.TopLectureDto;
import com.livo.project.admin.repository.ChartReportRepository;
import com.livo.project.admin.service.ChartService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ChartServiceImpl implements ChartService {

    private final ChartReportRepository repo;

    public ChartServiceImpl(ChartReportRepository repo) {
        this.repo = repo;
    }

    @Override
    @Cacheable(cacheNames = "chartTopLecturesV2", key = "#from + '_' + #to + '_' + #limit")
    public List<TopLectureDto> topLectures(LocalDate from, LocalDate to, int limit) {
        LocalDateTime f = atStartOfDay(from);
        LocalDateTime t = atEndOfDay(to);
        return repo.rawTopLectureStats(f, t, limit)
                .stream()
                .map(row -> {
                    int lectureId   = ((Number) row[0]).intValue();
                    long total      = row[1] == null ? 0L : ((Number) row[1]).longValue();
                    long confirmed  = row[2] == null ? 0L : ((Number) row[2]).longValue();
                    long canceled   = row[3] == null ? 0L : ((Number) row[3]).longValue();
                    long pending    = row[4] == null ? 0L : ((Number) row[4]).longValue();
                    long expired    = row[5] == null ? 0L : ((Number) row[5]).longValue();

                    double reservationRate = (total == 0) ? 0 : (double) confirmed / total;

                    return TopLectureDto.builder()
                            .lectureId(lectureId)
                            .total(total)
                            .confirmed(confirmed)
                            .canceled(canceled)
                            .pending(pending)
                            .expired(expired)
                            .reservationRate(reservationRate)
                            .build();
                })
                .toList();
    }

    @Override
    @Cacheable(cacheNames = "chartMonthlyRevenue", key = "#from + '_' + #to")
    public List<MonthlyRevenueDto> monthlyRevenue(LocalDate from, LocalDate to) {
        LocalDateTime f = atStartOfDay(from);
        LocalDateTime t = atEndOfDay(to);
        return repo.rawMonthlyRevenue(f, t)
                .stream()
                .map(row -> {
                    String ym              = row[0] == null ? null : row[0].toString(); // "YYYY-MM"
                    BigDecimal revenue     = row[1] == null ? BigDecimal.ZERO : new BigDecimal(row[1].toString());
                    long paidCount         = row[2] == null ? 0L : ((Number) row[2]).longValue();
                    long failOrCancelCount = row[3] == null ? 0L : ((Number) row[3]).longValue();
                    return MonthlyRevenueDto.builder()
                            .ym(ym)
                            .revenue(revenue)
                            .paidCount(paidCount)
                            .failOrCancelCount(failOrCancelCount)
                            .build();
                })
                .toList();
    }

    @Override
    @Cacheable(cacheNames = "chartMonthlySignups")
    public List<MonthlySignupDto> monthlySignups() {
        return repo.rawMonthlySignups()
                .stream()
                .map(row -> {
                    String ym   = row[0] == null ? null : row[0].toString(); // "YYYY-MM"
                    long count  = row[1] == null ? 0L : ((Number) row[1]).longValue();
                    return MonthlySignupDto.builder()
                            .ym(ym)
                            .newUsers(count)
                            .build();
                })
                .toList();
    }

    private static LocalDateTime atStartOfDay(LocalDate d) {
        return d.atStartOfDay();
    }

    private static LocalDateTime atEndOfDay(LocalDate d) {
        // inclusive end-of-day
        return d.plusDays(1).atStartOfDay().minusNanos(1);
    }
}
