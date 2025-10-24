package com.livo.project.admin.repository;

import com.livo.project.admin.repository.ChartReportRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ChartReportRepositoryImpl implements ChartReportRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Object[]> rawTopLectureStats(LocalDateTime from, LocalDateTime to, int limit) {
        String sql = """
            SELECT
                r.lectureId                                                   AS lecture_id,
                COUNT(*)                                                      AS total,
                SUM(CASE WHEN r.status IN ('PAID','CONFIRMED') THEN 1 ELSE 0 END) AS confirmed,
                SUM(CASE WHEN r.status = 'CANCEL'  THEN 1 ELSE 0 END)         AS canceled,
                SUM(CASE WHEN r.status = 'PENDING' THEN 1 ELSE 0 END)         AS pending,
                SUM(CASE WHEN r.status = 'EXPIRED' THEN 1 ELSE 0 END)         AS expired
            FROM reservation r
            WHERE r.createdAt BETWEEN :from AND :to
            GROUP BY r.lectureId
            ORDER BY confirmed DESC, total DESC
            LIMIT :limit
        """;
        return em.createNativeQuery(sql)
                .setParameter("from", from)
                .setParameter("to", to)
                .setParameter("limit", limit)
                .getResultList();
    }

    @Override
    public List<Object[]> rawMonthlySignups() {
        String sql = """
            SELECT
                DATE_FORMAT(MIN(r.createdAt), '%Y-%m') AS ym,
                COUNT(DISTINCT r.email)                AS new_users
            FROM reservation r
            GROUP BY DATE_FORMAT(MIN(r.createdAt), '%Y-%m')
            ORDER BY ym
        """;
        return em.createNativeQuery(sql).getResultList();
    }

    @Override
    public List<Object[]> rawMonthlyRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT " +
                "DATE_FORMAT(p.approvedAt, '%Y-%m') AS ym, " +
                "SUM(CASE WHEN p.status = 'DONE' THEN p.amount ELSE 0 END) AS revenue, " +
                "COUNT(CASE WHEN p.status = 'DONE' THEN 1 END) AS paid_count, " +
                "COUNT(CASE WHEN p.status IN ('FAILED','CANCELED') THEN 1 END) AS fail_or_cancel_count " +
                "FROM payment p " +
                "WHERE p.approvedAt IS NOT NULL " +
                "AND p.approvedAt BETWEEN ?1 AND ?2 " +   // ✅위치 기반 파라미터 사용
                "GROUP BY DATE_FORMAT(p.approvedAt, '%Y-%m') " +
                "ORDER BY ym";

        return em.createNativeQuery(sql)
                .setParameter(1, startDate)
                .setParameter(2, endDate)
                .getResultList();
    }
}