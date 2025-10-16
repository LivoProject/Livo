package com.livo.project.lecture.repository;

import com.livo.project.lecture.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    //이미 수강 중인지 확인 (중복 방지)
    boolean existsByLectureIdAndEmail(int lectureId, String email);
}
