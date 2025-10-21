// src/main/java/com/livo/project/auth/repository/UserRepository.java
package com.livo.project.auth.repository;

import com.livo.project.auth.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // --- 존재/중복 체크(기존) ---
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    boolean existsByPhone(String phone);

    // --- 조회(기존) ---
    Optional<User> findByEmail(String email);

    // --- 소셜 식별(기존) ---
    Optional<User> findByProviderAndProviderId(String provider, String providerId);

    // ====== 여기부터 추가 ======

    /** 이메일+프로바이더 기준으로 중복 체크(대소문자 무시 권장) */
    boolean existsByEmailIgnoreCaseAndProvider(String email, String provider);

    /** 이메일+프로바이더 기준으로 조회 */
    Optional<User> findByEmailIgnoreCaseAndProvider(String email, String provider);

    // Soft-delete(deletedAt) 사용 중이면 아래 버전으로 사용하세요.
    // boolean existsByEmailIgnoreCaseAndProviderAndDeletedAtIsNull(String email, String provider);
    // Optional<User> findByEmailIgnoreCaseAndProviderAndDeletedAtIsNull(String email, String provider);
}
