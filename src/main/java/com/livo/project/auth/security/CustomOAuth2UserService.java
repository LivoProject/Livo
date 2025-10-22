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
        String provider = userRequest.getClientRegistration().getRegistrationId().toUpperCase(Locale.ROOT);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = extractEmail(provider, attributes);
        String providerId = extractProviderId(provider, attributes);
        String displayName = extractDisplayName(provider, attributes);

        if (providerId == null) {
            throw new OAuth2AuthenticationException("providerId_not_found");
        }

        log.info("소셜 로그인 요청: provider={} providerId={} email={}", provider, providerId, email);

        // --- ① 기존 유저 조회 (provider + providerId 기준) ---
        User user = userRepository.findByProviderAndProviderId(provider, providerId).orElse(null);
        boolean isNew = false;

        // --- ② 신규 유저 생성 (providerId 기준으로 없을 경우만) ---
        if (user == null) {
            isNew = true;
            user = User.builder()
                    .email(email)
                    .password(null) // 소셜 로그인은 비밀번호 없음
                    .name(displayName != null ? displayName : "user")
                    .nickname(displayName != null ? displayName : UUID.randomUUID().toString().substring(0, 8))
                    .status(true)
                    .emailVerified(true)
                    .emailVerifiedAt(LocalDateTime.now())
                    .provider(provider)
                    .providerId(providerId)
                    .roleId(1)
                    .build();

            userRepository.save(user);
            log.info("신규 소셜 유저 등록 완료: provider={} providerId={} email={}", provider, providerId, email);
        } else {
            // 기존 유저는 정보 갱신
            if (displayName != null && !displayName.isBlank()) {
                user.setName(displayName);
                user.setNickname(displayName);
            }
            if (email != null && (user.getEmail() == null || user.getEmail().isBlank())) {
                user.setEmail(email);
            }
            user.setEmailVerified(true);
            if (user.getEmailVerifiedAt() == null)
                user.setEmailVerifiedAt(LocalDateTime.now());

            userRepository.save(user);
            log.info("기존 소셜 유저 로그인 성공: provider={} providerId={}", provider, providerId);
        }

        // --- ③ 권한 매핑 ---
        String role = mapRole(user.getRoleId());

        // --- ④ nameAttributeKey 계산 ---
        String nameAttrKey = Optional.ofNullable(
                userRequest.getClientRegistration()
                        .getProviderDetails()
                        .getUserInfoEndpoint()
                        .getUserNameAttributeName()
        ).filter(s -> !s.isBlank()).orElse(getDefaultNameAttrKey(provider));

        // --- ⑤ 세션 플래그 저장 ---
        session.setAttribute("isNewSocialUser", isNew);

        // --- ⑥ enriched attributes 구성 ---
        Map<String, Object> enriched = new HashMap<>(attributes);
        enriched.put("isNewUser", isNew);
        enriched.put("provider", provider);
        enriched.put("providerId", providerId);

        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority(role)),
                enriched,
                nameAttrKey
        );
    }

    private String getDefaultNameAttrKey(String provider) {
        return switch (provider.toUpperCase()) {
            case "GOOGLE" -> "sub";
            case "KAKAO", "NAVER" -> "id";
            default -> "sub";
        };
    }

    private String extractEmail(String provider, Map<String, Object> attributes) {
        if ("GOOGLE".equalsIgnoreCase(provider)) {
            return (String) attributes.get("email");
        }
        if ("KAKAO".equalsIgnoreCase(provider)) {
            Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
            return account == null ? null : (String) account.get("email");
        }
        if ("NAVER".equalsIgnoreCase(provider)) {
            Map<String, Object> resp = (Map<String, Object>) attributes.get("response");
            return resp == null ? null : (String) resp.get("email");
        }
        return null;
    }

    private String extractProviderId(String provider, Map<String, Object> attributes) {
        if ("GOOGLE".equalsIgnoreCase(provider)) {
            return (String) attributes.get("sub");
        }
        if ("KAKAO".equalsIgnoreCase(provider)) {
            Object id = attributes.get("id");
            return id == null ? null : String.valueOf(id);
        }
        if ("NAVER".equalsIgnoreCase(provider)) {
            Map<String, Object> resp = (Map<String, Object>) attributes.get("response");
            Object id = resp == null ? null : resp.get("id");
            return id == null ? null : String.valueOf(id);
        }
        return null;
    }

    private String extractDisplayName(String provider, Map<String, Object> attributes) {
        if ("GOOGLE".equalsIgnoreCase(provider)) {
            return (String) attributes.get("name");
        }
        if ("KAKAO".equalsIgnoreCase(provider)) {
            Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
            if (account != null && account.containsKey("profile")) {
                Map<String, Object> profile = (Map<String, Object>) account.get("profile");
                return (String) profile.get("nickname");
            }
        }
        if ("NAVER".equalsIgnoreCase(provider)) {
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
