package com.livo.project.lecture.service;

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
        return reservationRepository.existsByLectureIdAndEmail(lectureId, email);
    }

    // 리뷰 등록 시 reservationId 반환 (ReviewController용)
    @Override
    public Integer findReservationIdByEmailAndLectureId(String email, int lectureId, String provider) {
        return reservationRepository.findReservationIdByUserAndLecture(email, lectureId)
                .orElse(null);
    }
}
