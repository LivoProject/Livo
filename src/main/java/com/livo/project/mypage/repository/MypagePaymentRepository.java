package com.livo.project.mypage.repository;

import com.livo.project.payment.domain.Payment;
import com.livo.project.review.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface  MypagePaymentRepository  extends JpaRepository<Payment,Integer> {
   // 내 결제 내역 조회
   @Query("""
    SELECT p
    FROM Payment p
    JOIN p.reservation res
    JOIN res.user u
    WHERE u.email = :email
""")
   Page<Payment> findAllByEmail(@Param("email") String email, Pageable pageable);

}
