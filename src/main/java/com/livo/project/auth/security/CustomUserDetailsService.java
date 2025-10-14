package com.livo.project.auth.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.livo.project.auth.domain.entity.User;
import com.livo.project.auth.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없음: " + email));

        // status가 null이면 true로 간주 (엔티티 @PrePersist로 기본 true 세팅해두셨다면 사실상 안전)
        boolean enabled = u.getStatus() == null ? true : u.getStatus();

        // roleId 기반 간단 매핑 (추후 Role 엔티티로 대체 권장)
        String role = switch (u.getRoleId() == null ? 1 : u.getRoleId()) {
            case 9 -> "ADMIN";
            case 5 -> "MANAGER";
            default -> "USER";
        };

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getEmail())
                .password(u.getPassword())
                .roles(role)               // "ROLE_" 자동 접두
                .disabled(!enabled)        // 비활성 처리
                .accountLocked(false)
                .accountExpired(false)
                .credentialsExpired(false)
                .build();
    }
}
