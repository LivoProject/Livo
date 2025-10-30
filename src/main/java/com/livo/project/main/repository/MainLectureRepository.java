package com.livo.project.main.repository;

import com.livo.project.lecture.domain.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MainLectureRepository extends JpaRepository<Lecture, Integer> {
    // 추천 (랜덤 4개)
    @Query(value = "SELECT * FROM lecture WHERE visibility = 'ACTIVE' AND status != 'ENDED' ORDER BY RAND() LIMIT 4", nativeQuery = true)
    List<Lecture> findRandomLectures();

    // 인기 (평점 높은 순 10개)
    @Query(value = """
    SELECT l.* 
    FROM lecture l
    JOIN (
        SELECT lectureId, COUNT(*) AS likeCount
        FROM lecture_like
        GROUP BY lectureId
        ORDER BY likeCount DESC
        LIMIT 10
    ) AS topLiked ON l.lectureId = topLiked.lectureId
         WHERE l.visibility = 'ACTIVE'
            AND l.status != 'ENDED'
    ORDER BY topLiked.likeCount DESC
    """, nativeQuery = true)
    List<Lecture> findTop10LecturesByLikes();

}