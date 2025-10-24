package com.livo.project.report.controller;

import com.livo.project.auth.security.AppUserDetails;
import com.livo.project.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/lecture")
public class ReportController {

    private final ReportService reportService;

    // 민영 신고 등록: DB 저장!!
    @PostMapping("/content/{lectureId}/report")
    public String submitReport(@PathVariable int lectureId,
                               @RequestParam("reviewUId") int reviewUId,
                               @RequestParam("reportReason") String reportReason,
                               @RequestParam(value = "customReason", required = false) String customReason,
                               Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        Object principal = authentication.getPrincipal();
        String email = null;
        String provider = null;

        // 로컬 로그인
        if (principal instanceof AppUserDetails appUser) {
            email = appUser.getEmail();
            provider = appUser.getProvider();
        }
        // 소셜 로그인
        else if (principal instanceof org.springframework.security.oauth2.core.user.DefaultOAuth2User oAuthUser) {
            email = (String) oAuthUser.getAttribute("email");
            provider = (String) oAuthUser.getAttribute("provider");
        }

        if (email == null) {
            return "redirect:/auth/login";
        }

        reportService.saveReport(lectureId, reviewUId, reportReason, customReason, email, provider);

        return "redirect:/lecture/content/" + lectureId + "?reported=success#review";
    }
}
