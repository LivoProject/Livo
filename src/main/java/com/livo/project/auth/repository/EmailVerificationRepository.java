package com.livo.project.auth.repository;

import com.livo.project.auth.domain.entity.EmailVerification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    Optional<EmailVerification> findTopByEmailAndTokenHashAndConsumedAtIsNull(String email, String tokenHash);

    @Modifying
    @Query("update EmailVerification e set e.consumedAt = CURRENT_TIMESTAMP where e.email = :email and e.consumedAt is null")
    int consumeAllActiveByEmail(@Param("email") String email);
}
