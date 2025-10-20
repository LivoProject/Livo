package com.livo.project.auth.security;

import com.livo.project.auth.domain.entity.User;
import com.livo.project.auth.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final HttpSession session;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId(); // google, kakao, naver
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = extractEmail(provider, attributes);
        String providerId = extractProviderId(provider, attributes);
        String displayName = extractDisplayName(provider, attributes);

        if (email == null) {
            log.error("  소셜 로그인 실패: 이메일을 가져오지 못했습니다. provider={}", provider);
            throw new OAuth2AuthenticationException("email_not_found");
        }

        log.info("  소셜 로그인 요청: provider={} email={}", provider, email);
        User existing = userRepository.findByEmail(email).orElse(null);

        if (existing == null) {
            // 신규 소셜 유저 자동가입
            String randomPwd = UUID.randomUUID().toString();
            User newUser = User.builder()
                    .email(email)
                    .password("{noop}" + randomPwd)
                    .name(displayName != null ? displayName : "user")
                    .nickname(displayName != null ? displayName : "user")
                    .status(true)
                    .emailVerified(true)
                    .emailVerifiedAt(LocalDateTime.now())
                    .provider(provider)
                    .providerId(providerId)
                    .roleId(1)
                    .build();

            userRepository.save(newUser);
            userRepository.flush(); //  DB 반영 강제 확인용
            log.info(" 신규 소셜 유저 등록 완료: {} ({})", email, provider);

        } else {
            // 기존 유저면 업데이트
            log.info(" 기존 유저 로그인: {} (기존 provider={})", email, existing.getProvider());

            boolean providerMismatch =
                    existing.getProvider() == null
                            || "local".equalsIgnoreCase(existing.getProvider())
                            || !provider.equalsIgnoreCase(existing.getProvider());

            if (providerMismatch) {
                if (existing.getProvider() == null || "local".equalsIgnoreCase(existing.getProvider())) {
                    existing.setProvider(provider);
                    existing.setProviderId(providerId);
                    log.info(" 기존 로컬 계정을 소셜로 연동 완료: {} ({})", email, provider);
                } else {
                    session.setAttribute("conflictEmail", email);
                    session.setAttribute("conflictProvider", provider);
                    log.warn(" 이메일 충돌: {} (기존 provider={}, 현재={})",
                            email, existing.getProvider(), provider);
                    throw new OAuth2AuthenticationException(new OAuth2Error("account_conflict"));
                }
            }

            // 이름/닉네임 업데이트
            if (displayName != null && !displayName.isBlank()) {
                existing.setName(displayName);
                existing.setNickname(displayName);
                log.info("  이름/닉네임 업데이트: {} -> {}", email, displayName);
            }

            // 인증 상태 갱신
            existing.setEmailVerified(true);
            if (existing.getEmailVerifiedAt() == null) {
                existing.setEmailVerifiedAt(LocalDateTime.now());
            }

            // DB 반영
            log.info("  DB 업데이트 시도: {} (provider={}, providerId={})",
                    existing.getEmail(), existing.getProvider(), existing.getProviderId());
            userRepository.save(existing);
            userRepository.flush(); //   강제 flush
            log.info("  DB 업데이트 완료: {}", existing.getEmail());
        }

        String role = mapRole(existing != null ? existing.getRoleId() : 1);
        log.info("  최종 권한: {}", role);

        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority(role)),
                attributes,
                "sub"
        );
    }

    private String extractEmail(String provider, Map<String, Object> attributes) {
        if ("google".equals(provider)) {
            return (String) attributes.get("email");
        }
        if ("kakao".equals(provider)) {
            Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
            return account == null ? null : (String) account.get("email");
        }
        if ("naver".equals(provider)) {
            Map<String, Object> resp = (Map<String, Object>) attributes.get("response");
            return resp == null ? null : (String) resp.get("email");
        }
        return null;
    }

    private String extractProviderId(String provider, Map<String, Object> attributes) {
        if ("google".equals(provider)) {
            return (String) attributes.get("sub");
        }
        if ("kakao".equals(provider)) {
            Object id = attributes.get("id");
            return id == null ? null : String.valueOf(id);
        }
        if ("naver".equals(provider)) {
            Map<String, Object> resp = (Map<String, Object>) attributes.get("response");
            Object id = resp == null ? null : resp.get("id");
            return id == null ? null : String.valueOf(id);
        }
        return null;
    }

    private String extractDisplayName(String provider, Map<String, Object> attributes) {
        if ("google".equals(provider)) {
            return (String) attributes.get("name");
        }
        if ("kakao".equals(provider)) {
            Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
            if (account != null && account.containsKey("profile")) {
                Map<String, Object> profile = (Map<String, Object>) account.get("profile");
                return (String) profile.get("nickname");
            }
        }
        if ("naver".equals(provider)) {
            Map<String, Object> resp = (Map<String, Object>) attributes.get("response");
            return resp == null ? null : (String) resp.get("name");
        }
        return null;
    }

    private String mapRole(Integer roleId) {
        if (roleId != null) {
            if (roleId == 3) return "ROLE_ADMIN";
            if (roleId == 2) return "ROLE_MANAGER";
        }
        return "ROLE_USER";
    }
}
