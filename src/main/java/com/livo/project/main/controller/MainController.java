package com.livo.project.main.controller;

import com.livo.project.main.dto.MainDto;
import com.livo.project.main.service.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final MainService mainService;

    // 오디오 true=켜짐 false=꺼짐
    private static final boolean BGM_ENABLED = true;

    @GetMapping({"/", "/main"})
    public String mainPage(Model model, Authentication auth) {
        // 서비스에서 카테고리 + 추천강좌 + 인기강좌 한 번에 조회
        MainDto mainData = mainService.getMainPageData();

        // 모델에 담아서 JSP or Thymeleaf로 전달
        model.addAttribute("categories", mainData.getCategories());
        model.addAttribute("recommendedLectures", mainData.getRecommendedLectures());
        //model.addAttribute("popularLectures", mainData.getPopularLectures());
        model.addAttribute("notices", mainData.getNotices());

        boolean isLoggedIn = (auth != null && auth.isAuthenticated());
        // 로그인했고, 개발자 토글이 켜져 있으면 재생 허용
        model.addAttribute("BGM_ALLOWED", BGM_ENABLED && isLoggedIn);

        return "main/main";
    }
}