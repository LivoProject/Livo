package com.livo.project.auth.security;

import com.livo.project.auth.domain.entity.User;
import com.livo.project.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        // 원본 OIDC 사용자 정보 조회
        OidcUser oidcUser = super.loadUser(userRequest);

        String provider   = userRequest.getClientRegistration().getRegistrationId(); // google
        Map<String, Object> attributes = oidcUser.getAttributes();

        // 표준 OIDC 클레임
        String email       = (String) attributes.get("email");
        String providerId  = (String) attributes.get("sub");
        String displayName = (String) attributes.getOrDefault("name", "user");

        if (email == null) {
            throw new IllegalStateException("OIDC email not found");
        }

        // 신규/기존 사용자 처리 (네 OAuth2UserService와 동일 로직)
        User existing = userRepository.findByEmail(email).orElse(null);

        if (existing == null) {
            String randomPwd = UUID.randomUUID().toString();
            User newUser = User.builder()
                    .email(email)
                    .password("{noop}" + randomPwd)
                    .name(displayName)
                    .nickname(displayName)
                    .status(true)
                    .emailVerified(true)
                    .emailVerifiedAt(LocalDateTime.now())
                    .provider(provider)
                    .providerId(providerId)
                    .roleId(1)
                    .build();
            userRepository.saveAndFlush(newUser);
            log.info(" [OIDC] 신규 소셜 유저 등록: {} ({})", email, provider);
        } else {
            boolean needsLink =
                    existing.getProvider() == null ||
                            "local".equalsIgnoreCase(existing.getProvider());

            if (needsLink) {
                existing.setProvider(provider);
                existing.setProviderId(providerId);
                log.info(" [OIDC] 기존 로컬 계정에 소셜 연동: {} ({})", email, provider);
            } else if (!provider.equalsIgnoreCase(existing.getProvider())) {
                // 다른 소셜과 충돌 시 정책에 따라 처리(여기선 막음)
                log.warn(" [OIDC] 이메일 충돌: {} (기존={}, 현재={})",
                        email, existing.getProvider(), provider);
                throw new IllegalStateException("account_conflict");
            }

            // 이름/닉네임 업데이트(옵션)
            existing.setName(displayName);
            existing.setNickname(displayName);
            existing.setEmailVerified(true);
            if (existing.getEmailVerifiedAt() == null) {
                existing.setEmailVerifiedAt(LocalDateTime.now());
            }

            userRepository.saveAndFlush(existing);
            log.info(" [OIDC] 기존 유저 업데이트 완료: {}", email);
        }

        // 권한 매핑(간단히 ROLE_USER)
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        // nameAttributeKey는 OIDC에선 "sub" 사용
        return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo(), "sub");
    }
}
