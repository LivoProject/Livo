// com.livo.project.auth.security.CustomUserDetailsService.java
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
        // 로그인 이메일 소문자 정규화
        String normalized = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);

        User u = userRepository.findByEmail(normalized)
                .orElseThrow(() -> new UsernameNotFoundException("잘못된 자격 증명입니다."));

        // ★ 스프링 기본 User 대신 커스텀 UserDetails 반환(닉네임/권한/상태 포함)
        return new AppUserDetails(u);
    }
}
