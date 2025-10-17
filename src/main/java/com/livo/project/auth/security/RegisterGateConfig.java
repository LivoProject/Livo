// com/livo/project/config/RegisterGateConfig.java
package com.livo.project.auth.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class RegisterGateConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
                if ("POST".equalsIgnoreCase(req.getMethod()) && "/auth/register".equals(req.getRequestURI())) {
                    HttpSession session = req.getSession(false);
                    String verified = session == null ? null : (String) session.getAttribute("VERIFIED_EMAIL");
                    String formEmail = req.getParameter("email");
                    if (verified == null || formEmail == null || !verified.equalsIgnoreCase(formEmail)) {
                        // 인증 안 됨 → 폼으로 되돌리기
                        res.sendRedirect("/auth/register?needVerify=1");
                        return false;
                    }
                }
                return true;
            }
        });
    }
}
