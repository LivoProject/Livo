package com.livo.project.mypage.repository;

import com.livo.project.lecture.domain.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;


public interface MypageReservationRepository extends JpaRepository<Reservation, Integer> {
    //내 강좌 조회
    @Query("SELECT r FROM Reservation r WHERE r.email = :email")
    Page<Reservation> findAllByEmail(@Param("email") String email, @Param("provider") String provider, Pageable pageable);

    // 내 강좌 예약 취소
    @Modifying
    @Query("DELETE FROM Reservation r WHERE r.user.email = :email AND r.lectureId = :lectureId")
    void deleteByEmailAndLectureId(@Param("lectureId") Integer lectureId, @Param("email") String email, @Param("provider") String provider);



}
