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
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidc = super.loadUser(userRequest);

        //  기본 정보 추출
        String provider = userRequest.getClientRegistration().getRegistrationId().toUpperCase(Locale.ROOT); // GOOGLE 등
        Map<String, Object> claims = new HashMap<>(oidc.getClaims());
        String providerId = asString(claims.get("sub"));
        String email = (String) claims.get("email");
        String displayName = (String) claims.getOrDefault("name", "user");

        if (providerId == null || providerId.isBlank()) {
            throw new OAuth2AuthenticationException("invalid_provider_id");
        }

        log.info("[OIDC] 로그인 요청: provider={} providerId={} email={}", provider, providerId, email);

        //  provider + providerId 기준으로만 식별
        User user = userRepository.findByProviderAndProviderId(provider, providerId).orElse(null);
        boolean isNew = false;

        if (user == null) {
            isNew = true;

            //  닉네임 중복 방지 추가
            String baseNickname = (displayName != null && !displayName.isBlank())
                    ? displayName
                    : "user";
            String uniqueNickname = generateUniqueNickname(baseNickname, provider);

            user = User.builder()
                    .provider(provider)
                    .providerId(providerId)
                    .email(email)
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .name(displayName)
                    .nickname(uniqueNickname)   //  변경
                    .status(true)
                    .emailVerified(email != null)
                    .emailVerifiedAt(email != null ? LocalDateTime.now() : null)
                    .roleId(1)
                    .build();
            userRepository.save(user);
            log.info(" [OIDC] 신규 소셜 유저 생성: provider={} providerId={} email={} nickname={}",
                    provider, providerId, email, uniqueNickname);
        } else {
            //  기존 유저 정보 갱신
            boolean changed = false;
            if (displayName != null && !displayName.equals(user.getName())) {
                user.setName(displayName);
                user.setNickname(displayName);
                changed = true;
            }
            if (email != null && (user.getEmail() == null || !email.equals(user.getEmail()))) {
                user.setEmail(email);
                changed = true;
            }
            if (changed) {
                userRepository.save(user);
                log.info("[OIDC] 기존 유저 정보 갱신 완료: id={} email={}", user.getId(), user.getEmail());
            }
        }

        //  권한 매핑
        var authorities = List.of(new SimpleGrantedAuthority(mapRole(user.getRoleId())));

        //  Claims 확장 (isNewUser → LoginSuccessHandler에서 사용)
        claims.put("provider", provider);
        claims.put("providerId", providerId);
        claims.put("isNewUser", isNew);

        //  OIDC 객체 재구성
        OidcIdToken raw = oidc.getIdToken();
        Map<String, Object> mergedClaims = new HashMap<>(raw.getClaims());
        mergedClaims.putAll(claims);

        OidcIdToken mergedIdToken = new OidcIdToken(
                raw.getTokenValue(),
                raw.getIssuedAt(),
                raw.getExpiresAt(),
                mergedClaims
        );

        OidcUserInfo mergedUserInfo = new OidcUserInfo(claims);

        return new DefaultOidcUser(authorities, mergedIdToken, mergedUserInfo, "sub");
    }

    private String asString(Object o) {
        return o == null ? null : String.valueOf(o);
    }

    private String mapRole(Integer roleId) {
        if (roleId != null) {
            if (roleId == 3) return "ROLE_ADMIN";
            if (roleId == 2) return "ROLE_MANAGER";
        }
        return "ROLE_USER";
    }
    private String generateUniqueNickname(String base, String provider) {
        String nickname = base;
        int counter = 1;

        while (userRepository.existsByNickname(nickname)) {
            nickname = base + "_" + provider.toLowerCase() + counter;
            counter++;
        }
        return nickname;
    }
}
