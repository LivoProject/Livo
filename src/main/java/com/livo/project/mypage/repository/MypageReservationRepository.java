package com.livo.project.mypage.repository;

import com.livo.project.lecture.domain.Reservation;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MypageReservationRepository extends JpaRepository<Reservation, Integer> {


    // 내 예약 강좌
    @Query("SELECT r FROM Reservation r WHERE r.user.email = :email AND (r.status = 'CONFIRMED' OR r.status = 'PAID')")
    Page<Reservation> findAllByEmailAndProvider(@Param("email") String email, @Param("provider") String provider, Pageable pageable);


    // 예약 취소
    @Modifying
    @Query("UPDATE Reservation r SET r.status = 'CANCEL' WHERE r.lecture.lectureId = :lectureId AND r.user.email = :email")
    int cancelByLectureIdAndEmail(@Param("lectureId") Integer lectureId,
                                  @Param("email") String email);

    // 좋아요한 강좌가 예약된 강좌인지 확인
    boolean existsByLecture_LectureIdAndUser_Email(Integer lectureId, String email);



}