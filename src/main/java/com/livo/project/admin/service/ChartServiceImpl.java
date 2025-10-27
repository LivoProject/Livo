package com.livo.project.admin.service;

import com.livo.project.admin.domain.dto.InstructorOpsDto;
import com.livo.project.admin.domain.dto.MonthlyRevenueDto;
import com.livo.project.admin.domain.dto.MonthlySignupDto;
import com.livo.project.admin.domain.dto.TopLectureDto;
import com.livo.project.admin.repository.ChartReportRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

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

    /** ✅ 월별 신규 가입자 (프론트가 넘기는 yyyy-MM ~ yyyy-MM) */
    @Override
    @Cacheable(cacheNames = "chartMonthlySignups", key = "#fromYm + '_' + #toYm")
    public List<MonthlySignupDto> monthlySignups(String fromYm, String toYm) {
        YearMonth from = YearMonth.parse(fromYm);
        YearMonth to   = YearMonth.parse(toYm);
        if (to.isBefore(from)) { var tmp = from; from = to; to = tmp; }

        // [from 1일 00:00, to 다음달 1일 00:00) 미포함 상한
        LocalDateTime start        = from.atDay(1).atStartOfDay();
        LocalDateTime endExclusive = to.plusMonths(1).atDay(1).atStartOfDay();

        // [ "YYYY-MM", new_users ] 로 내려옴
        List<Object[]> rows = repo.rawMonthlySignups(start, endExclusive);

        Map<String, Long> countByYm = rows.stream()
                .collect(Collectors.toMap(
                        r -> String.valueOf(r[0]),
                        r -> ((Number) r[1]).longValue()
                ));

        // 빈 월을 0으로 채워 정렬된 결과 구성
        List<MonthlySignupDto> out = new ArrayList<>();
        for (YearMonth cur = from; !cur.isAfter(to); cur = cur.plusMonths(1)) {
            String ym = cur.toString(); // "YYYY-MM"
            out.add(MonthlySignupDto.builder()
                    .ym(ym)
                    .newUsers(countByYm.getOrDefault(ym, 0L))
                    .build());
        }
        return out;
    }
    @Override
    public List<InstructorOpsDto> instructorOps(LocalDate from, LocalDate to, int limit) {
        LocalDateTime f = from.atStartOfDay();
        LocalDateTime tExclusive = to.plusDays(1).atStartOfDay();

        return repo.rawInstructorOps(f, tExclusive, limit).stream()
                .map(row -> {
                    String tutorName = row[0] == null ? "(미등록)" : row[0].toString();
                    long lectures     = row[1] == null ? 0L : ((Number) row[1]).longValue();
                    long total        = row[2] == null ? 0L : ((Number) row[2]).longValue();
                    long confirmed    = row[3] == null ? 0L : ((Number) row[3]).longValue();
                    long pending      = row[4] == null ? 0L : ((Number) row[4]).longValue();
                    long canceled     = row[5] == null ? 0L : ((Number) row[5]).longValue();
                    BigDecimal revenue = row[6] == null ? BigDecimal.ZERO : new BigDecimal(row[6].toString());

                    double fillRate = total == 0 ? 0 : (double) confirmed / total;

                    return InstructorOpsDto.builder()
                            .tutorName(tutorName)
                            .lectures(lectures)
                            .total(total)
                            .confirmed(confirmed)
                            .pending(pending)
                            .canceled(canceled)
                            .revenue(revenue)
                            .fillRate(fillRate)
                            .build();
                })
                .toList();
    }


    // --------------------------------

    private static LocalDateTime atStartOfDay(LocalDate d) {
        return d.atStartOfDay();
    }

    private static LocalDateTime atEndOfDay(LocalDate d) {
        // inclusive end-of-day
        return d.plusDays(1).atStartOfDay().minusNanos(1);
    }
}
