package com.livo.project.admin.repository;

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
            r.lectureId AS lectureId,
            COUNT(*) AS total,
            SUM(CASE WHEN r.status IN ('CONFIRMED','PAID') THEN 1 ELSE 0 END) AS confirmed,
            SUM(CASE WHEN r.status IN ('CANCEL','EXPIRED') THEN 1 ELSE 0 END) AS canceled,
            SUM(CASE WHEN r.status = 'PENDING' THEN 1 ELSE 0 END) AS pending,
            0 AS expired
        FROM reservation r
        WHERE r.createdAt BETWEEN ?1 AND ?2
        GROUP BY r.lectureId
        ORDER BY confirmed DESC, total DESC
        """;
        return em.createNativeQuery(sql)
                .setParameter(1, from)
                .setParameter(2, to)
                .setMaxResults(limit) // LIMIT :limit 대신 setMaxResults 사용
                .getResultList();
    }


    //  월별 가입자 수 (로컬타임 기준) — users.created_at에서 월별 집계
    @Override
    public List<Object[]> rawMonthlySignups(LocalDateTime from, LocalDateTime toExclusive) {
        String sql = """
        SELECT DATE_FORMAT(u.created_at, '%Y-%m') AS ym,
               COUNT(*) AS new_users
        FROM `user` u                      
        WHERE u.created_at >= ?1
          AND u.created_at <  ?2
        GROUP BY ym
        ORDER BY ym
        """;
        return em.createNativeQuery(sql)
                .setParameter(1, from)
                .setParameter(2, toExclusive)
                .getResultList();
    }


    @Override
    public List<Object[]> rawMonthlyRevenue(LocalDateTime from, LocalDateTime toExclusive) {
        String sql =
                "SELECT " +
                        "  DATE_FORMAT(p.approvedAt, '%Y-%m') AS ym, " +
                        "  SUM(CASE WHEN p.status = 'SUCCESS' THEN p.amount ELSE 0 END)                         AS revenue, " +
                        "  SUM(CASE WHEN p.status = 'SUCCESS' THEN 1       ELSE 0 END)                         AS paid_count, " +
                        "  SUM(CASE WHEN p.status IN ('FAIL','CANCEL') THEN 1 ELSE 0 END)                      AS fail_or_cancel_count " +
                        "FROM payment p " +
                        "WHERE p.approvedAt IS NOT NULL " +
                        "  AND p.approvedAt >= ?1 " +
                        "  AND p.approvedAt <  ?2 " +
                        "GROUP BY DATE_FORMAT(p.approvedAt, '%Y-%m') " +
                        "ORDER BY ym";

        return em.createNativeQuery(sql)
                .setParameter(1, from)
                .setParameter(2, toExclusive)
                .getResultList();
    }
    @Override
    public List<Object[]> rawInstructorOps(LocalDateTime from, LocalDateTime toExclusive, int limit) {
        String sql =
                "SELECT " +
                        "   l.tutorName                        AS tutor_name, " +
                        "   COUNT(DISTINCT l.lectureId)        AS lectures, " +
                        "   COUNT(r.reservationId)             AS total_resv, " +
                        "   SUM(CASE WHEN r.status IN ('CONFIRMED','PAID') THEN 1 ELSE 0 END) AS confirmed, " +
                        "   SUM(CASE WHEN r.status = 'PENDING' THEN 1 ELSE 0 END)             AS pending, " +
                        "   SUM(CASE WHEN r.status IN ('CANCEL','EXPIRED') THEN 1 ELSE 0 END) AS canceled, " +
                        "   SUM(CASE WHEN p.status = 'SUCCESS' THEN p.amount ELSE 0 END)      AS revenue " +
                        "FROM lecture l " +
                        "LEFT JOIN reservation r " +
                        "  ON r.lectureId = l.lectureId " +
                        " AND r.createdAt >= ?1 AND r.createdAt < ?2 " +
                        "LEFT JOIN payment p " +
                        "  ON p.lectureId = l.lectureId " +
                        " AND p.approvedAt >= ?1 AND p.approvedAt < ?2 " +
                        "GROUP BY l.tutorName " +
                        "ORDER BY confirmed DESC, total_resv DESC " +
                        "LIMIT ?3";

        return em.createNativeQuery(sql)
                .setParameter(1, from)
                .setParameter(2, toExclusive)
                .setParameter(3, limit)
                .getResultList();
    }
}
