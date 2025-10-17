package com.livo.project.auth.control;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // 루트나 /home 접근 시 역할에 따라 분기
    @GetMapping({ "/home"})
    public String main(Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            boolean isAdmin = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch("ROLE_ADMIN"::equals);

            boolean isManager = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch("ROLE_MANAGER"::equals);

            if (isAdmin) {
                return "redirect:/admin/dashboard"; // 관리자라면 관리자 대시보드
            } else if (isManager) {
                return "redirect:/manager"; // 매니저라면 매니저 페이지
            }
        }
        // 일반 사용자 또는 비로그인 상태라면 기본 메인 페이지 렌더링
        return "main/main"; // /WEB-INF/views/main/main.jsp
    }
}
