package com.livo.project.payment.repository;

import com.livo.project.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment,Integer> {
    Optional<Payment> findByOrderId(String paymentKey);
    Optional<Payment> findByPaymentKey(String paymentKey);
    List<Payment> findByUser_EmailOrderByApprovedAtDesc(String email);

    Payment findTopByReservation_ReservationIdOrderByApprovedAtDesc(int reservationId);
}
