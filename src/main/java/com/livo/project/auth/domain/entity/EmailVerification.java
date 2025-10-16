// src/main/java/com/livo/project/auth/domain/entity/EmailVerification.java
package com.livo.project.auth.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "email_verification")
public class EmailVerification {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 254)
    private String email;

    @Column(name = "code_hash", length = 64, nullable = false)
    private String codeHash;

    @Column(name = "code_expires_at")
    private LocalDateTime codeExpiresAt;

    @Column(name = "code_consumed_at")
    private LocalDateTime codeConsumedAt;

    @Column(name = "attempt_count")
    private Integer attemptCount;

    @Column(name = "last_sent_at")
    private LocalDateTime lastSentAt;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
