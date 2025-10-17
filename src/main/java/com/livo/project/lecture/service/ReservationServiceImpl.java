package com.livo.project.lecture.service;

import com.livo.project.lecture.domain.Reservation;
import com.livo.project.lecture.repository.ReservationRepository;
import org.springframework.stereotype.Service;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationServiceImpl(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Override
    public void saveReservation(int lectureId, String email) {

        //중복방지인데, 이거 모달창 해야 하나... 일단 나중에!
        if (reservationRepository.existsByLectureIdAndEmail(lectureId, email)) {
            System.out.println("이미 수강 중인 강의입니다."); //모달로?
            return;
        }

        //수강신청
        Reservation reservation = new Reservation();
        reservation.setLectureId(lectureId);
        reservation.setEmail(email);
        reservation.setStatus(Reservation.Status.CONFIRMED);

        reservationRepository.save(reservation);
        System.out.println("무료강의 수강신청 완료!"); //모달로?

    }

    @Override
    public boolean isUserEnrolled(int lectureId, String email) {
        return reservationRepository.existsByLectureIdAndEmail(lectureId, email);
    }
}
