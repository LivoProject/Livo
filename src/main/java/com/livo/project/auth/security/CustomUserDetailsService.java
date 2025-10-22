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
        if (email == null || email.isBlank()) {
            throw new UsernameNotFoundException("이메일을 입력해주세요.");
        }

        //  로그인 이메일 정규화
        String normalized = email.trim().toLowerCase(Locale.ROOT);

        //  반드시 provider='LOCAL' 계정만 허용
        User u = userRepository
                .findByEmailIgnoreCaseAndProvider(normalized, "LOCAL")
                .orElseThrow(() ->
                        new UsernameNotFoundException("이메일 또는 비밀번호가 올바르지 않습니다.")
                );

        // 3️ 상태/인증 체크
        if (Boolean.FALSE.equals(u.getStatus())) {
            throw new UsernameNotFoundException("비활성화된 계정입니다.");
        }
        if (!Boolean.TRUE.equals(u.getEmailVerified())) {
            throw new UsernameNotFoundException("이메일 인증이 필요합니다.");
        }

        //  커스텀 UserDetails 래퍼 리턴
        return new AppUserDetails(u);
    }
}
