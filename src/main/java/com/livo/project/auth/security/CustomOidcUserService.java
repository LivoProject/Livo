package com.livo.project.auth.security;

import com.livo.project.auth.domain.entity.User;
import com.livo.project.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
//import org.springframework.security.oauth2.core.oidc.user.OidcUserInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // ★ BCrypt 주입

    @Transactional
    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidc = super.loadUser(userRequest);

        // 1) 공급자/클레임
        String provider = Optional.ofNullable(userRequest.getClientRegistration().getRegistrationId())
                .orElse("UNKNOWN").toUpperCase(Locale.ROOT); // GOOGLE/KAKAO/NAVER
        Map<String, Object> claims = new HashMap<>(oidc.getClaims());
        String providerId  = asString(claims.get("sub"));              // 필수
        String email       = Optional.ofNullable((String) claims.get("email"))
                .map(s -> s.toLowerCase(Locale.ROOT)).orElse(null);
        String displayName = (claims.get("name") instanceof String s && !s.isBlank()) ? s : "user";

        if (providerId == null || providerId.isBlank()) {
            throw new OAuth2AuthenticationException("invalid_provider_id");
        }

        // 2) 1순위: provider + providerId
        User user = userRepository.findByProviderAndProviderId(provider, providerId).orElse(null);
        boolean isNew = false;

        if (user == null && email != null) {
            // 3) 2순위: email (이메일 전역 유니크 유지)
            user = userRepository.findByEmail(email).orElse(null);

            if (user != null) {
                // 이미 이메일로 존재 → 자동 연결 정책
                // - provider가 비어있거나 같은 provider면 providerId 세팅
                // - 다른 provider로 이미 연결돼 있으면 자동 병합 금지 (보안)
                if (user.getProvider() == null || user.getProvider().equalsIgnoreCase(provider)) {
                    user.setProvider(provider);
                    user.setProviderId(providerId);
                    user.setEmailVerified(true);
                    if (user.getEmailVerifiedAt() == null) user.setEmailVerifiedAt(LocalDateTime.now());
                    userRepository.save(user);
                    log.info(" [OIDC] 기존 이메일 계정에 공급자 연결: email={}, provider={}", email, provider);
                } else {
                    throw new OAuth2AuthenticationException(
                            new org.springframework.security.oauth2.core.OAuth2Error("account_link_required"),
                            "해당 이메일은 다른 로그인 방식과 이미 연결되어 있습니다. 로그인 후 '계정 연결'에서 연동하세요."
                    );
                }
            }
        }

        if (user == null) {
            // 4) 정말로 신규
            isNew = true;
            user = User.builder()
                    .provider(provider)
                    .providerId(providerId)
                    .email(email) // null 허용
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .name(displayName)
                    .nickname(displayName)
                    .status(true)
                    .emailVerified(email != null)
                    .emailVerifiedAt(email != null ? LocalDateTime.now() : null)
                    .roleId(1)
                    .build();
            userRepository.save(user);
            log.info(" [OIDC] 신규 소셜 유저 생성: provider={} providerId={} email={}", provider, providerId, email);
        } else {
            // 5) 선택 동기화 (필요 시)
            boolean changed = false;
            if (!Objects.equals(user.getName(), displayName)) { user.setName(displayName); changed = true; }
            if (!Objects.equals(user.getNickname(), displayName)) { user.setNickname(displayName); changed = true; }
            if (email != null && !Objects.equals(user.getEmail(), email)) { user.setEmail(email); changed = true; }
            if (!Boolean.TRUE.equals(user.getEmailVerified()) && email != null) {
                user.setEmailVerified(true);
                if (user.getEmailVerifiedAt() == null) user.setEmailVerifiedAt(LocalDateTime.now());
                changed = true;
            }
            if (changed) userRepository.save(user);
            log.info(" [OIDC] 기존 소셜 유저 동기화: id={} email={}", user.getId(), user.getEmail());
        }

        // 6) 권한/리턴
        var authorities = List.of(new SimpleGrantedAuthority(mapRole(user.getRoleId())));
        claims.put("provider", provider);
        claims.put("providerId", providerId);
        claims.put("isNewUser", isNew);

        OidcIdToken idToken = oidc.getIdToken();
        var userInfo = oidc.getUserInfo();
        return new DefaultOidcUser(authorities, idToken, userInfo, "sub");
    }
    private String asString(Object o) { return o == null ? null : String.valueOf(o); }

    private String mapRole(Integer roleId) {
        if (roleId != null) {
            if (roleId == 3) return "ROLE_ADMIN";
            if (roleId == 2) return "ROLE_MANAGER";
        }
        return "ROLE_USER";
    }
}
