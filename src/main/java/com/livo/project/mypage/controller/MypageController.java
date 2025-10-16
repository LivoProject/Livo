package com.livo.project.mypage.controller;

import com.livo.project.mypage.dto.MypageDto;
import com.livo.project.mypage.service.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MypageController {

    private final MypageService mypageService;

    @GetMapping
    public String home(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login"; // 로그인 안되어 있으면 로그인 페이지로
        }

        String email = userDetails.getUsername(); // 로그인 시 username 필드에 email이 들어간 구조일 경우
        MypageDto mypage = mypageService.getUserData(email);

        model.addAttribute("mypage", mypage);
        return "mypage/index";
    }

    @GetMapping("/lecture")
    public String lecture() {
        return "mypage/lecture";
    }

    @GetMapping("/info")
    public String info() {
        return "mypage/info";
    }

    @GetMapping("/bookmark")
    public String bookmark() {
        return "mypage/bookmark";
    }

    @GetMapping("/review")
    public String review() {
        return "mypage/review";
    }
}
