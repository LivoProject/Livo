package com.livo.project.auth.security;

import com.livo.project.auth.domain.entity.User;
import com.livo.project.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 로그인 입력 이메일은 소문자 정규화 권장
        String normalized = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);

        User u = userRepository.findByEmail(normalized)
                .orElseThrow(() -> new UsernameNotFoundException("잘못된 자격 증명입니다."));

        // status (null → true), emailVerified(미인증 차단), deletedAt(있으면 잠금 취급)
        boolean activeStatus   = (u.getStatus() == null) || Boolean.TRUE.equals(u.getStatus());
        boolean emailVerified  = Boolean.TRUE.equals(u.getEmailVerified());
        boolean notDeleted     = (u.getDeletedAt() == null);

        boolean enabled        = activeStatus && emailVerified && notDeleted;
        boolean accountLocked  = !notDeleted;  // 삭제 상태를 잠금으로 해석 (정책에 맞게 조정 가능)

        // roleId 기반 권한 매핑 (roles()는 "ROLE_" 자동 접두)
        String role = switch (u.getRoleId() == null ? 1 : u.getRoleId()) {
            case 9 -> "ADMIN";
            case 5 -> "MANAGER";
            default -> "USER";
        };

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getEmail())   // DB 저장값(이미 소문자 저장 권장)
                .password(u.getPassword())
                .roles(role)                  // "ROLE_" 접두 자동 부여
                .disabled(!enabled)           // ← 이메일 미인증/비활성/삭제 시 로그인 차단
                .accountLocked(accountLocked)
                .accountExpired(false)
                .credentialsExpired(false)
                .build();
    }
}
