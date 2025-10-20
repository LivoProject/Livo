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
        MainDto mainDto = mainService.getMainPageData();

        model.addAttribute("categories", mainDto.getCategories());
        model.addAttribute("recommendedLectures", mainDto.getRecommendedLectures());
        model.addAttribute("notices", mainDto.getNotices());
        model.addAttribute("BGM_ALLOWED", BGM_ENABLED);

        return "main/index";
    }
}