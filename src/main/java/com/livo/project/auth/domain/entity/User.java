package com.livo.project.auth.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "`user`",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_email", columnNames = "email"),
                @UniqueConstraint(name = "uq_user_nickname", columnNames = "nickname"),
                @UniqueConstraint(name = "uq_user_phone", columnNames = "phone"),
                @UniqueConstraint(name = "uq_user_provider_pid", columnNames = {"provider", "providerId"})
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 254, unique = true)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

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

    // 이메일 인증 컬럼
    @Column(name = "email_verified", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean emailVerified = false;

    @Column(name = "email_verified_at")
    private LocalDateTime emailVerifiedAt;

    @PrePersist
    void prePersistDefaultStatus() {
        if (status == null) status = true;
        if (emailVerified == null) emailVerified = false;

    }

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "role_id", nullable = false)
    private Integer roleId;

    @Column(length = 30, nullable = false)
    private String provider;

    @Column(name = "providerId", length = 128, nullable = false)
    private String providerId;
}
