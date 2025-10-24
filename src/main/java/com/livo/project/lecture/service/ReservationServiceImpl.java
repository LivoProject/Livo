package com.livo.project.lecture.service;

import com.livo.project.auth.domain.entity.User;
import com.livo.project.auth.repository.UserRepository;
import com.livo.project.lecture.domain.Lecture;
import com.livo.project.lecture.domain.Reservation;
import com.livo.project.lecture.repository.LectureRepository;
import com.livo.project.lecture.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final LectureRepository lectureRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void saveReservation(int lectureId, String email, String provider) {

        //수강신청
        Reservation reservation = new Reservation();
        reservation.setLectureId(lectureId);
        reservation.setEmail(email);
        reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);

        reservationRepository.save(reservation);

        // 강좌 수강 인원 증가
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의를 찾을 수 없습니다."));
        lecture.setReservationCount(lecture.getReservationCount() + 1);
        lectureRepository.save(lecture);

    }

    @Override
    public boolean isUserEnrolled(int lectureId, String email, String provider) {
        // CANCEL 제외하고 수강 여부 확인
        return reservationRepository.existsByEmailAndLectureIdAndStatusNot(
                email, lectureId, Reservation.Status.CANCEL
        );
    }

    // 리뷰 등록 시 reservationId 반환 (ReviewController용)
    @Override
    public Integer findReservationIdByEmailAndLectureId(String email, int lectureId, String provider) {
        return reservationRepository.findReservationIdByUserAndLecture(email, lectureId)
                .orElse(null);
    }
    @Transactional
    @Override
    public int createPendingReservation(int lectureId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원 정보 없음"));
        if(reservationRepository.existsByLectureIdAndEmail(lectureId, email)) {
            throw new IllegalArgumentException("이미 예약된 강의입니다.");
        }
        Reservation reservation = new Reservation();
        reservation.setLectureId(lectureId);
        reservation.setUser(user);
        reservation.setEmail(email);
        reservation.setStatus(Reservation.ReservationStatus.PENDING);

        reservationRepository.save(reservation);
        return reservation.getReservationId();
    }
}
