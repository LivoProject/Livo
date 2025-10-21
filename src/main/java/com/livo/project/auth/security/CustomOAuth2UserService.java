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
import java.util.*;

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
            log.error("❌ 소셜 로그인 실패: 이메일을 가져오지 못했습니다. provider={}", provider);
            throw new OAuth2AuthenticationException("email_not_found");
        }

        log.info("✅ 소셜 로그인 요청: provider={} email={}", provider, email);

        // --- 기존 회원 조회 ---
        User user = userRepository.findByEmail(email).orElse(null);
        boolean isNew = false;

        if (user == null) {
            // 신규 유저 자동 가입
            String randomPwd = UUID.randomUUID().toString();
            user = User.builder()
                    .email(email)
                    .password("{noop}" + randomPwd)
                    .name(displayName != null ? displayName : "user")
                    .nickname(displayName != null ? displayName : "user")
                    .status(true)
                    .emailVerified(true)
                    .emailVerifiedAt(LocalDateTime.now())
                    .provider(provider)
                    .providerId(providerId)
                    .roleId(1) // 기본 USER
                    .build();
            userRepository.saveAndFlush(user);
            isNew = true;
            log.info("🎉 신규 소셜 유저 등록 완료: {} ({})", email, provider);
        } else {
            log.info("🔁 기존 유저 로그인 시도: {} (기존 provider={})", email, user.getProvider());

            boolean providerMismatch =
                    user.getProvider() == null
                            || "local".equalsIgnoreCase(user.getProvider())
                            || !provider.equalsIgnoreCase(user.getProvider());

            if (providerMismatch) {
                if (user.getProvider() == null || "local".equalsIgnoreCase(user.getProvider())) {
                    user.setProvider(provider);
                    user.setProviderId(providerId);
                    log.info("🔗 기존 로컬 계정을 소셜로 연동 완료: {} ({})", email, provider);
                } else {
                    session.setAttribute("conflictEmail", email);
                    session.setAttribute("conflictProvider", provider);
                    log.warn("⚠️ 이메일 충돌: {} (기존 provider={}, 현재={})",
                            email, user.getProvider(), provider);
                    throw new OAuth2AuthenticationException(new OAuth2Error("account_conflict"));
                }
            }

            if (displayName != null && !displayName.isBlank()) {
                user.setName(displayName);
                user.setNickname(displayName);
            }

            user.setEmailVerified(true);
            if (user.getEmailVerifiedAt() == null)
                user.setEmailVerifiedAt(LocalDateTime.now());

            userRepository.saveAndFlush(user);
            log.info("✅ 기존 소셜 유저 DB 업데이트 완료: {}", email);
        }

        // --- 권한 매핑 ---
        String role = mapRole(user.getRoleId());

        // --- nameAttributeKey 계산 (버전 호환 안전)
        provider = provider == null ? "UNKNOWN" : provider.toUpperCase();
        String defaultNameAttrKey;
        switch (provider) {
            case "GOOGLE":
                defaultNameAttrKey = "sub";
                break;
            case "KAKAO":
            case "NAVER":
                defaultNameAttrKey = "id";
                break;
            default:
                defaultNameAttrKey = "sub";
                break;
        }

        String nameAttrKey = Optional.ofNullable(
                        userRequest.getClientRegistration()
                                .getProviderDetails()
                                .getUserInfoEndpoint()
                                .getUserNameAttributeName()
                ).filter(s -> !s.isBlank())
                .orElse(defaultNameAttrKey);

        log.info("🧩 nameAttributeKey resolved: {}", nameAttrKey);

        // --- 세션에 신규 여부 플래그 저장 (마이페이지 등에서 활용 가능) ---
        session.setAttribute("isNewSocialUser", isNew);

        // --- 최종 OAuth2User 구성 ---
        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority(role)),
                attributes,
                nameAttrKey
        );
    }

    private String extractEmail(String provider, Map<String, Object> attributes) {
        if ("google".equalsIgnoreCase(provider)) {
            return (String) attributes.get("email");
        }
        if ("kakao".equalsIgnoreCase(provider)) {
            Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
            return account == null ? null : (String) account.get("email");
        }
        if ("naver".equalsIgnoreCase(provider)) {
            Map<String, Object> resp = (Map<String, Object>) attributes.get("response");
            return resp == null ? null : (String) resp.get("email");
        }
        return null;
    }

    private String extractProviderId(String provider, Map<String, Object> attributes) {
        if ("google".equalsIgnoreCase(provider)) {
            return (String) attributes.get("sub");
        }
        if ("kakao".equalsIgnoreCase(provider)) {
            Object id = attributes.get("id");
            return id == null ? null : String.valueOf(id);
        }
        if ("naver".equalsIgnoreCase(provider)) {
            Map<String, Object> resp = (Map<String, Object>) attributes.get("response");
            Object id = resp == null ? null : resp.get("id");
            return id == null ? null : String.valueOf(id);
        }
        return null;
    }

    private String extractDisplayName(String provider, Map<String, Object> attributes) {
        if ("google".equalsIgnoreCase(provider)) {
            return (String) attributes.get("name");
        }
        if ("kakao".equalsIgnoreCase(provider)) {
            Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
            if (account != null && account.containsKey("profile")) {
                Map<String, Object> profile = (Map<String, Object>) account.get("profile");
                return (String) profile.get("nickname");
            }
        }
        if ("naver".equalsIgnoreCase(provider)) {
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
