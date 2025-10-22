package com.livo.project.mypage.repository;

import com.livo.project.review.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface  MypageReviewRepository extends JpaRepository <Review,Integer> {

    // 내 리뷰 조회
    @Query("SELECT r FROM Review r WHERE r.reservation.email = :email")
    Page<Review> findAllByEmail(@Param("email") String email, Pageable pageable);
}
