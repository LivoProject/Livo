package com.livo.project.mypage.repository;

import com.livo.project.lecture.domain.Lecture;
import com.livo.project.mypage.domain.entity.LectureProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MypageProgressRepository extends JpaRepository<LectureProgress, Long> {
    // Optional<LectureProgress> findByLectureAndEmail(Lecture lecture, String email);

    @Query("""
                SELECT lp
                FROM LectureProgress lp
                WHERE lp.email = :email
                  AND lp.lecture = :lecture
            """)
    Optional<LectureProgress> findByLectureAndUser(
            @Param("lecture") Lecture lecture,
            @Param("email") String email,
            @Param("provider") String provider // provider는 그냥 받기만, 쿼리에 안 써도 됨
    );

    // 최근 학습 강의
    @Query("""
                SELECT lp
                FROM LectureProgress lp
                JOIN FETCH lp.lecture
                WHERE lp.email = :email
                ORDER BY lp.lastAccessedAt DESC
            """)
    List<LectureProgress> findTopWithLectureByEmail(@Param("email") String email);

    // 학습 목표
    @Query("SELECT SUM(lp.lastWatchedTime) FROM LectureProgress lp WHERE lp.email = :email")
    Double sumTotalWatchedTime(@Param("email") String email);

    int countByEmailAndProgressPercentGreaterThanEqual(String email, double percent);

    @Query("""
                SELECT COUNT(DISTINCT DATE(lp.lastAccessedAt))
                FROM LectureProgress lp
                WHERE lp.email = :email
                  AND MONTH(lp.lastAccessedAt) = MONTH(CURRENT_DATE)
                  AND YEAR(lp.lastAccessedAt) = YEAR(CURRENT_DATE)
            """)
    int countDistinctDaysThisMonth(@Param("email") String email);


}
