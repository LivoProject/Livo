package com.livo.project.mypage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")
public class MypageController {

    @GetMapping
    public String home() {
        return "mypage/index";
    }

    @GetMapping("/lecture")
    public String lectures() {
        return "mypage/lecture";
    }

    @GetMapping("/info")
    public String info() {
        return "mypage/info";
    }

    @GetMapping("/bookmark")
    public String bookmarks() {
        return "mypage/bookmark";
    }
}
