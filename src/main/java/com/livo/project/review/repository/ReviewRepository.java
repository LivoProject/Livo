package com.livo.project.review.repository;

import com.livo.project.review.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    // 특정 강좌의 리뷰를 불러오기 (lectureId로 검색)
    @Query("""
        SELECT r 
        FROM Review r 
        JOIN Reservation res ON r.reservationId = res.reservationId
        WHERE res.lectureId = :lectureId
        ORDER BY r.createdAt DESC
        """)
    List<Review> findReviewsByLectureId(@Param("lectureId") int lectureId);

    // 특정 강좌의 리뷰 평균
    @Query("""
        SELECT AVG(r.reviewStar)
        FROM Review r
        JOIN Reservation res ON r.reservationId = res.reservationId
        WHERE res.lectureId = :lectureId
        """)
    Double findAverageStarByLectureId(@Param("lectureId") int lectureId);

    // 리뷰 페이징
    @Query("""
        SELECT r 
        FROM Review r 
        JOIN Reservation res ON r.reservationId = res.reservationId
        WHERE res.lectureId = :lectureId
        ORDER BY r.createdAt DESC
        """)
    Page<Review> findReviewsByLectureIdPaged(@Param("lectureId") int lectureId, Pageable pageable);

}
