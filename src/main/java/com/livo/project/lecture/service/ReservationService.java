package com.livo.project.lecture.service;

import com.livo.project.lecture.domain.Reservation;

import java.util.Optional;

public interface ReservationService {

    void saveReservation(int lectureId, String email, String provider);

    // 로그인 유저가 특정 강의 수강 중인지
    boolean isUserEnrolled(int lectureId, String email, String provider);

    // 리뷰 등록 시 reservationId 가져오기 (ReviewController)
    Integer findReservationIdByEmailAndLectureId(String email, int lectureId, String provider);

    int createPendingReservation(int lectureId, String email);

    Reservation.ReservationStatus getReservationStatus(String Email, int lectureId);
    Optional<Reservation> findPendingReservation(int lectureId, String email);
}
