package com.livo.project.main.controller;

import com.livo.project.main.domain.dto.MainDto;
import com.livo.project.main.service.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

    // 오디오 true=켜짐 false=꺼짐
    private static final boolean BGM_ENABLED = true;

    private final MainService mainService;

    @GetMapping({"/", "/index", "/main"})
    public String mainPage(Model model) {
        // 서비스에서 카테고리 + 추천강좌 + 인기강좌 한 번에 조회
        MainDto mainDto = mainService.getMainPageData();

        // 모델에 담아서 JSP로 전달
        model.addAttribute("categories", mainDto.getCategories());
        model.addAttribute("recommendedLectures", mainDto.getRecommendedLectures());
        model.addAttribute("notices", mainDto.getNotices());

        return "main/index";
    }
}