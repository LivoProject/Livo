package com.livo.project.auth.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.livo.project.lecture.domain.Reservation;
import com.livo.project.payment.domain.Payment;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        name = "`user`",
        uniqueConstraints = {

                @UniqueConstraint(name = "uq_user_nickname", columnNames = "nickname"),
                @UniqueConstraint(name = "uq_user_phone", columnNames = "phone"),
                @UniqueConstraint(name = "uq_user_provider_pid", columnNames = {"provider", "providerId"}),
                @UniqueConstraint(name = "uq_user_email_local", columnNames = "email_local")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(length = 254)
    private String email; // 로컬 + 소셜 공용 (UNIQUE 아님)

    //  DB에 Stored Generated Column (if(provider='LOCAL', email, NULL))
    @Column(name = "email_local", insertable = false, updatable = false)
    private String emailLocal;

    @Column(length = 255)
    private String password; // 로컬만 사용 (소셜은 NULL)

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 40)
    private String nickname;

    @Column(length = 20)
    private String phone;

    private LocalDate birth;

    public enum Gender { M, F }

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean status = true;


    @Column(name = "email_verified", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean emailVerified = false;

    @Column(name = "email_verified_at")
    private LocalDateTime emailVerifiedAt;


    @Column(name = "role_id", nullable = false)
    private Integer roleId;

    @Column(length = 30, nullable = false)
    private String provider; // LOCAL, GOOGLE, KAKAO, NAVER ...

    @Column(name = "providerId", length = 128)
    private String providerId; // 로컬은 NULL or UUID

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;


    @PrePersist
    void prePersistDefaults() {
        if (status == null) status = true;
        if (emailVerified == null) emailVerified = false;
    }

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Payment> payments;
}
