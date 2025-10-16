package com.livo.project.lecture.controller;

import com.livo.project.lecture.service.LectureLikeService;
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
    public String toggleLike(@PathVariable int lectureId) {

        String userEmail = "test@livo.com"; //임시 유저 로그인 연결 필요

        boolean isLiked = lectureLikeService.toggleLike(lectureId, userEmail);

        return isLiked ? "liked" : "unliked";
    }

    //좋아요 여부 확인
    @GetMapping("/like/check/{lectureId}")
    @ResponseBody
    public boolean checkLike(@PathVariable int lectureId) {

        String userEmail = "test@livo.com"; //임시 유저 로그인 연결 필요

        return lectureLikeService.isLiked(lectureId, userEmail);
    }

}
