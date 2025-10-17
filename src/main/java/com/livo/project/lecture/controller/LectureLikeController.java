package com.livo.project.lecture.controller;

import com.livo.project.lecture.service.LectureLikeService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/lecture")
public class LectureLikeController {

    private final LectureLikeService lectureLikeService;

    public LectureLikeController(LectureLikeService lectureLikeService) {
        this.lectureLikeService = lectureLikeService;
    }

    //AJAX 요청 처리
    @PostMapping("/like/{lectureId}")
    @ResponseBody
    public String toggleLike(@PathVariable int lectureId,
                             @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return "unauthorized"; // 문자열로 반환
        }

        String userEmail = userDetails.getUsername();

        boolean isLiked = lectureLikeService.toggleLike(lectureId, userEmail);
        return isLiked ? "liked" : "unliked";
    }

    //좋아요 여부 확인
    @GetMapping("/like/check/{lectureId}")
    @ResponseBody
    public boolean checkLike(@PathVariable int lectureId,
                             @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return false; // 로그인 안 되어 있으면 false 반환
        }

        String userEmail = userDetails.getUsername();
        return lectureLikeService.isLiked(lectureId, userEmail);
    }

}
