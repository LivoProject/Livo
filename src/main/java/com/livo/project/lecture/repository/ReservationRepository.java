package com.livo.project.lecture.repository;

import com.livo.project.auth.domain.entity.User;
import com.livo.project.lecture.domain.Lecture;
import com.livo.project.lecture.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    // 이미 수강 중인지 확인 (중복 방지 + 후기 등록 조건!!)
    boolean existsByLectureIdAndEmail(int lectureId, String email);

    // 취소되지 않은 수강 여부 확인
    boolean existsByEmailAndLectureIdAndStatusNot(String email, int lectureId, Reservation.ReservationStatus status);

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
    boolean existsByEmailAndLectureIdAndStatus(String email, int lectureId, Reservation.ReservationStatus status);

    int lecture(Lecture lecture);
}
