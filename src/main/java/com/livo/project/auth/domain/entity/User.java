package com.livo.project.auth.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "`user`",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_email", columnNames = "email"),
                @UniqueConstraint(name = "uq_user_nickname", columnNames = "nickname"),
                @UniqueConstraint(name = "uq_user_phone", columnNames = "phone")
        }
)
@Getter @Setter @NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 254)
    private String email;

    @Column(nullable = false, length = 60)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(length = 20)
    private String phone;

    private LocalDate birth;

    public enum Gender { M, F }

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private Boolean status;

    @PrePersist
    void prePersistDefaultStatus() {
        if (status == null) status = true;

    }

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "role_id", nullable = false)
    private Integer roleId;
}
