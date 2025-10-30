package com.livo.project.mypage.repository;

import com.livo.project.lecture.domain.Lecture;
import com.livo.project.mypage.repository.projection.LikedLectureProjection;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MypageLectureRepository extends JpaRepository<Lecture, Integer> {
//
//    // 추천 강좌
//    @Query(value = "SELECT * FROM lecture ORDER BY RAND() LIMIT 3", nativeQuery = true)
//    List<Lecture> findRandomLectures();

    // 좋아요한 강좌
    @Query(
            value = """
                        SELECT
                            l.lectureId AS lectureId,
                            l.title AS title,
                            l.tutorName AS tutorName,
                            l.price AS price,                        
                            l.thumbnailUrl AS thumbnailUrl,
                            COALESCE(lp.progressPercent, 0) AS progressPercent,
                            CASE
                                WHEN (
                                    SELECT COUNT(1)
                                    FROM reservation r2
                                    WHERE r2.lectureId = l.lectureId
                                      AND r2.email = u.email
                                      AND r2.status != 'CANCEL'
                                ) > 0 THEN TRUE
                                ELSE FALSE
                            END AS reserved
                        FROM lecture l
                        JOIN lecture_like ll
                            ON l.lectureId = ll.lectureId
                        JOIN `user` u
                            ON ll.email = u.email
                        LEFT JOIN lecture_progress lp
                            ON lp.lectureId = l.lectureId
                            AND lp.email = u.email
                        WHERE u.email = :email
                          AND u.provider = :provider
                          AND l.visibility = 'ACTIVE'
                          AND l.status = 'OPEN'  -- 모두 담을건지 확인해보기
                        ORDER BY ll.createdAt DESC
                    """,
            countQuery = """
                        SELECT COUNT(*)
                        FROM lecture l
                        JOIN lecture_like ll
                            ON l.lectureId = ll.lectureId
                        JOIN `user` u
                            ON ll.email = u.email
                        WHERE u.email = :email
                          AND u.provider = :provider
                          AND l.visibility = 'ACTIVE'
                          AND l.status = 'OPEN'
                    """,
            nativeQuery = true
    )
    Page<LikedLectureProjection> findLikedLecturesWithProgress(
            @Param("email") String email,
            @Param("provider") String provider,
            Pageable pageable
    );

    // 좋아요한 강좌 3개
    @Query(value = """
            SELECT 
                l.lectureId        AS lectureId,
                l.title            AS title,
                l.tutorName        AS tutorName,
                l.price            AS price,           
                l.thumbnailUrl     AS thumbnailUrl,
                COALESCE(lp.progressPercent, 0) AS progressPercent
            FROM lecture_like ll
            JOIN lecture l 
              ON l.lectureId = ll.lectureId
            JOIN `user` u
              ON u.email = ll.email
            LEFT JOIN lecture_progress lp 
              ON lp.lectureId = l.lectureId 
             AND lp.email = u.email
            WHERE u.email = :email
              AND u.provider = :provider 
              AND l.visibility = 'ACTIVE'
              AND l.status = 'OPEN'
            ORDER BY ll.createdAt DESC
            LIMIT 3
            """,
            nativeQuery = true)
    List<LikedLectureProjection> findTop2LikedLecturesByEmailWithProgress(
            @Param("email") String email,
            @Param("provider") String provider
    );

    // 좋아요 해제
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM lecture_like WHERE lectureId = :lectureId AND email = :email", nativeQuery = true)
    void deleteLikeByLectureIdAndEmail(@Param("lectureId") Integer lectureId, @Param("email") String email, @Param("provider") String provider);

    // 좋아요 한 강좌 정렬
    @Query(value = """
    SELECT
        l.lectureId AS lectureId,
        l.title AS title,
        l.tutorName AS tutorName,
        l.price AS price,
        l.thumbnailUrl AS thumbnailUrl,
        COALESCE(lp.progressPercent, 0) AS progressPercent,
        CASE
            WHEN EXISTS (
                SELECT 1
                FROM reservation r2
                WHERE r2.lectureId = l.lectureId
                  AND r2.email = u.email
                  AND r2.status != 'CANCEL'
            ) THEN TRUE
            ELSE FALSE
        END AS reserved
    FROM lecture l
    JOIN lecture_like ll ON l.lectureId = ll.lectureId
    JOIN user u ON ll.email = u.email
    LEFT JOIN lecture_progress lp
        ON lp.lectureId = l.lectureId AND lp.email = u.email
    LEFT JOIN lecture_like ll2
        ON ll2.lectureId = l.lectureId
    WHERE u.email = :email
      AND u.provider = :provider
      AND l.visibility = 'ACTIVE'
      AND l.status = 'OPEN'
    GROUP BY l.lectureId, l.title, l.tutorName, l.price, l.thumbnailUrl, lp.progressPercent, reserved
    ORDER BY
        CASE WHEN :sort = 'popular' THEN COUNT(DISTINCT ll2.likeId) END DESC,
        CASE WHEN :sort = 'old' THEN MIN(ll.createdAt) END ASC,
        CASE WHEN :sort = 'new' OR :sort IS NULL THEN MAX(ll.createdAt) END DESC
    LIMIT 20
""", nativeQuery = true)
    List<LikedLectureProjection> findLikedLecturesDynamicSortSimple(
            @Param("email") String email,
            @Param("provider") String provider,
            @Param("sort") String sort
    );



}