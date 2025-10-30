package com.livo.project.payment.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.livo.project.auth.domain.entity.User;
import com.livo.project.lecture.domain.Lecture;
import com.livo.project.lecture.domain.Reservation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer paymentId;

    private String paymentKey;
    private String orderId;
    private String orderName;
    private String method;
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PaymentStatus status; // PENDING, SUCCESS, CANCEL, FAIL, REFUND

    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime canceledAt;
    /** 관계 매핑 **/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lectureId", nullable = false)
    private Lecture lecture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservationId", nullable = false)
    @JsonIgnore
    private Reservation reservation;

    public enum PaymentStatus {
        PENDING, SUCCESS, CANCEL, FAIL, REFUND
    }
}

