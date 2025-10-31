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

import java.util.Optional;

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
                email, lectureId, Reservation.ReservationStatus.CANCEL
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
        // 가장 최근 예약 검색
        Optional<Reservation> existingOpt = reservationRepository
                .findTopByLecture_LectureIdAndUser_EmailOrderByCreatedAtDesc(lectureId, email);

        if (existingOpt.isPresent()) {
            Reservation existing = existingOpt.get();

            // 이미 수강 중이면 새 예약 불가
            if (existing.getStatus() == Reservation.ReservationStatus.CONFIRMED || existing.getStatus() == Reservation.ReservationStatus.PAID) {
                return existing.getReservationId();
            }

            // 예약 대기(PENDING) 재시도 → 재사용
            if (existing.getStatus() == Reservation.ReservationStatus.PENDING) {
                return existing.getReservationId();
            }

            // 환불(CANCEL)된 기록 → 되살려서 다시 예약 상태로
            if (existing.getStatus() == Reservation.ReservationStatus.CANCEL) {
                existing.setStatus(Reservation.ReservationStatus.PENDING);
                reservationRepository.save(existing);
                return existing.getReservationId();
            }
        }

        Reservation reservation = new Reservation();
        reservation.setLectureId(lectureId);
        reservation.setUser(user);
        reservation.setEmail(email);
        reservation.setStatus(Reservation.ReservationStatus.PENDING);

        reservationRepository.save(reservation);
        return reservation.getReservationId();
    }

    @Override
    public Reservation.ReservationStatus getReservationStatus(String email, int lectureId) {
        return reservationRepository.findTopByLecture_LectureIdAndUser_EmailOrderByCreatedAtDesc(lectureId, email)
                .map(Reservation::getStatus)
                .orElse(null);
    }

    @Override
    public Optional<Reservation> findPendingReservation(int lectureId, String email) {
        return reservationRepository.findByLecture_LectureIdAndUser_EmailAndStatus(lectureId, email, Reservation.ReservationStatus.PENDING);
    }
}
