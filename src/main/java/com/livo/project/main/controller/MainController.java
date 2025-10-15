package com.livo.project.main.controller;

import com.livo.project.main.dto.MainDTO;
import com.livo.project.main.service.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequiredArgsConstructor // ✅ 자동으로 생성자 주입 (final 필드 자동 주입)
public class MainController {

    private final MainService mainService;

    @GetMapping("index.html")
    public String mainPage(Model model) {
        MainDTO mainData = mainService.getMainPageData();
        model.addAttribute("categories", mainData.getCategories());
        model.addAttribute("lectures", mainData.getLectures());
        return "main/main";
    }
}