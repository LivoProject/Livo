package com.livo.project.lecture.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.livo.project.auth.domain.entity.User;
import com.livo.project.payment.domain.Payment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name="reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reservationId;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status = ReservationStatus.PENDING;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    @CreationTimestamp
   // @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    @UpdateTimestamp
 //   @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;

    private int lectureId; // FK (lecture)
    private String email;  // FK (user)

    // Lecture와 연결
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lectureId", referencedColumnName = "lectureId", insertable = false, updatable = false)
    private Lecture lecture;

    //User랑 연결
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "email", referencedColumnName = "email", insertable = false, updatable = false)
    private User user;

    @OneToOne(mappedBy = "reservation", fetch = FetchType.LAZY)
    @JsonIgnore
    private Payment payment;

    public enum ReservationStatus {
        PENDING, PAID, CANCEL, CONFIRMED, EXPIRED
    }
}
