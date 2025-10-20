package com.livo.project.maindashboard.controller;

import com.livo.project.maindashboard.domain.dto.MainDashBoardDto;
import com.livo.project.maindashboard.service.MainDashBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;

@Controller
@RequiredArgsConstructor
public class MainDashBoardController {

    private final MainDashBoardService mainService;

    // 오디오 true=켜짐 false=꺼짐
    private static final boolean BGM_ENABLED = true;

    @GetMapping({"/", "/dashboard"})
    public String mainPage(Model model, Authentication auth) {
        // 서비스에서 카테고리 + 추천강좌 + 인기강좌 한 번에 조회
        MainDashBoardDto mainDashBoardDto = mainService.getMainPageData();


        // 모델에 담아서 JSP로 전달
        model.addAttribute("categories", mainDashBoardDto.getCategories());
        model.addAttribute("recommendedLectures", mainDashBoardDto.getRecommendedLectures());
        //model.addAttribute("popularLectures", mainDashBoardDto.getPopularLectures());
        model.addAttribute("notices", mainDashBoardDto.getNotices());

        boolean isLoggedIn = (auth != null && auth.isAuthenticated());
        // 로그인했고, 개발자 토글이 켜져 있으면 재생 허용
        model.addAttribute("BGM_ALLOWED", BGM_ENABLED && isLoggedIn);


        return "maindashboard/dashboard";
    }
}