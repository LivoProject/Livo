package com.livo.project.lecture.repository;

import com.livo.project.lecture.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    //이미 수강 중인지 확인 (중복 방지 + 후기 등록 조건!!)
    boolean existsByLectureIdAndUser_Id(int lectureId, long userId);

    // 리뷰 등록 시 실제 reservationId 찾기
    @Query("""
        SELECT r.reservationId 
        FROM Reservation r 
        WHERE r.user.email = :email 
        AND r.lectureId = :lectureId
        """)
    Optional<Integer> findReservationIdByUserAndLecture(@Param("email") String email,
                                                        @Param("lectureId") int lectureId);
    Optional<Reservation> findByReservationId(int reservationId);
}
