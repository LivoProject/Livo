package com.livo.project.auth.security;

import com.livo.project.auth.domain.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        HttpSession session = request.getSession();
        session.setAttribute("PLAY_BGM_ONCE", Boolean.TRUE); // 메인 브금 1회 재생용 플래그

        Object principal = authentication.getPrincipal();
        String redirectUrl = request.getContextPath() + "/";

        try {
            //  (1) OAuth2User일 경우 (소셜 로그인)
            if (principal instanceof org.springframework.security.oauth2.core.user.DefaultOAuth2User oAuthUser) {
                Map<String, Object> attrs = oAuthUser.getAttributes();
                String provider = (String) attrs.get("provider");
                String providerId = (String) attrs.get("providerId");
                Boolean isNewUser = (Boolean) attrs.get("isNewUser");

                log.info("소셜 로그인 성공: provider={} providerId={} isNew={}", provider, providerId, isNewUser);

                // 이메일이 없거나 신규 가입이면 온보딩 페이지로
                if (isNewUser != null && isNewUser) {
                    redirectUrl = request.getContextPath() + "/onboarding/email";
                } else {
                    redirectUrl = request.getContextPath() + "/mypage";
                }
            }
            //  (2) UserDetails (로컬 로그인)
            else if (principal instanceof UserDetails userDetails) {
                log.info("로컬 로그인 성공: {}", userDetails.getUsername());
                redirectUrl = request.getContextPath() + "/"; // 기본 메인
            }
        } catch (Exception e) {
            log.error("로그인 성공 후 리다이렉트 처리 중 오류", e);
            redirectUrl = request.getContextPath() + "/";
        }

        //  (3) 세션에 로그인 시간 기록 (옵션)
        session.setAttribute("LOGIN_SUCCESS_AT", LocalDateTime.now());

        //  (4) 리다이렉트 실행
        response.sendRedirect(redirectUrl);
    }
}
