package com.livo.project.lecture.controller;

import com.livo.project.auth.security.AppUserDetails;
import com.livo.project.lecture.service.LectureLikeService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/lecture")
public class LectureLikeController {

    private final LectureLikeService lectureLikeService;

    public LectureLikeController(LectureLikeService lectureLikeService) {
        this.lectureLikeService = lectureLikeService;
    }

    // ✅ 좋아요 토글
    @PostMapping("/like/{lectureId}")
    @ResponseBody
    public String toggleLike(@PathVariable int lectureId,
                             Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "unauthorized";
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
            return "unauthorized";
        }

        boolean isLiked = lectureLikeService.toggleLike(lectureId, email, provider);
        return isLiked ? "liked" : "unliked";
    }

    // ✅ 좋아요 여부 확인
    @GetMapping("/like/check/{lectureId}")
    @ResponseBody
    public boolean checkLike(@PathVariable int lectureId,
                             Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();
        String email = null;
        String provider = null;

        if (principal instanceof AppUserDetails appUser) {
            email = appUser.getEmail();
            provider = appUser.getProvider();
        } else if (principal instanceof org.springframework.security.oauth2.core.user.DefaultOAuth2User oAuthUser) {
            email = (String) oAuthUser.getAttribute("email");
            provider = (String) oAuthUser.getAttribute("provider");
        }

        if (email == null) {
            return false;
        }

        return lectureLikeService.isLiked(lectureId, email, provider);
    }

}
