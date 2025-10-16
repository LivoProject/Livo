package com.livo.project.lecture.service;

import com.livo.project.lecture.domain.Reservation;

public interface ReservationService {
    void saveReservation(int lectureId, String email);
}
