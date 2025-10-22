package com.livo.project.auth.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        var session = request.getSession(true);
        session.setAttribute("PLAY_BGM_ONCE", Boolean.TRUE);

        // 역할별 세션 만료
        var set = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        int adminSecs = 15 * 60, managerSecs = 30 * 60, userSecs = 60 * 60;
        if (set.contains("ROLE_ADMIN"))       session.setMaxInactiveInterval(adminSecs);
        else if (set.contains("ROLE_MANAGER")) session.setMaxInactiveInterval(managerSecs);
        else                                   session.setMaxInactiveInterval(userSecs);

        String redirect;
        try {
            // 역할 기반 라우팅만 유지
            if (set.contains("ROLE_ADMIN")) {
                redirect = "/admin/dashboard";
            } else if (set.contains("ROLE_MANAGER")) {
                redirect = "/manager";
            } else {
                // ✅ 일반 사용자(로컬/소셜 모두) → 메인으로
                redirect = "/";      // /main 으로 보내고 싶으면 "/main" 으로 변경
            }
        } catch (Exception e) {
            log.error("성공 후 리다이렉트 결정 중 오류", e);
            redirect = "/";
        }

        String url = request.getContextPath() + redirect;
        log.info("[LoginSuccessHandler] redirect to {}", url);
        response.sendRedirect(url);
    }
}