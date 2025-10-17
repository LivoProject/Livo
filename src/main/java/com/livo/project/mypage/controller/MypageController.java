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

        String email = userDetails.getUsername(); // 현재 로그인한 사용자의 email을 가져옴
        MypageDto mypage = mypageService.getUserData(email); // 위에서 얻은 이메일을 서비스에 넘겨서 해당 사용자의 MypageDto를 가져옴

        model.addAttribute("mypage", mypage);
        return "mypage/index";
    }

    @GetMapping("/lecture")
    public String lecture() {
        return "mypage/lecture";
    }

    @GetMapping("/info")
    public String info(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String email = userDetails.getUsername();
        MypageDto mypage = mypageService.getUserData(email);

        model.addAttribute("mypage", mypage);
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
