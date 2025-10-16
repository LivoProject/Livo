// com/livo/project/auth/repository/EmailVerificationRepository.java
package com.livo.project.auth.repository;

import com.livo.project.auth.domain.entity.EmailVerification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    // 최신 미소비 코드(쿨다운 계산용)
    Optional<EmailVerification> findTopByEmailAndCodeConsumedAtIsNullOrderByCodeExpiresAtDesc(String email);

    // 코드 일치 검사
    Optional<EmailVerification> findTopByEmailAndCodeHashAndCodeConsumedAtIsNull(String email, String codeHash);

    // 동일 이메일의 활성 코드 전부 소비(무효화)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update EmailVerification e
           set e.codeConsumedAt = CURRENT_TIMESTAMP
         where e.email = :email
           and e.codeConsumedAt is null
    """)
    int consumeAllActiveCodesByEmail(@Param("email") String email);

    // 시도 횟수 +1
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update EmailVerification e
           set e.attemptCount = coalesce(e.attemptCount,0) + 1
         where e.id = :id
    """)
    int incrementAttemptCount(@Param("id") Long id);
}
