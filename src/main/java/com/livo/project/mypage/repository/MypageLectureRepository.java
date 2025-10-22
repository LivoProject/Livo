package com.livo.project.mypage.repository;

import com.livo.project.lecture.domain.Lecture;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MypageLectureRepository extends JpaRepository<Lecture, Integer> {

    // 추천 강좌
    @Query(value = "SELECT * FROM lecture ORDER BY RAND() LIMIT 3", nativeQuery = true)
    List<Lecture> findRandomLectures();

    // 좋아요한 강좌
    @Query(value = """
             SELECT l.* 
            FROM lecture l
            JOIN lecture_like ll ON l.lectureId = ll.lectureId
            WHERE ll.email = :email
            ORDER BY ll.createdAt DESC
            """,
            countQuery = """
                    SELECT COUNT(*) 
                    FROM lecture l
                    JOIN lecture_like ll ON l.lectureId = ll.lectureId
                    WHERE ll.email = :email
                    """,
            nativeQuery = true)
    Page<Lecture> findLikedLecturesByEmail(@Param("email") String email, @Param("provider") String provider, Pageable pageable);

    // 좋아요한 강좌 최신 2개
    @Query(value = """
            SELECT l.* 
            FROM lecture l
            JOIN lecture_like ll ON l.lectureId = ll.lectureId
            WHERE ll.email = :email
            ORDER BY ll.createdAt DESC
            LIMIT 2
            """, nativeQuery = true)
    List<Lecture> findTop2LikedLecturesByEmail(@Param("email") String email, @Param("provider") String provider);

    // 좋아요 해제
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM lecture_like WHERE lectureId = :lectureId AND email = :email", nativeQuery = true)
    void deleteLikeByLectureIdAndEmail(@Param("lectureId") Integer lectureId, @Param("email") String email, @Param("provider") String provider);

}