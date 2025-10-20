// src/main/java/com/livo/project/auth/security/LoginSuccessHandler.java
package com.livo.project.auth.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        // 메인에서 '한 번만' 브금 재생하도록 세션 플래그 셋
        HttpSession session = request.getSession();
        session.setAttribute("PLAY_BGM_ONCE", Boolean.TRUE);

        // 메인으로 이동 (메인 페이지에서만 브금 코드가 있음)
        response.sendRedirect(request.getContextPath() + "/");
    }
}
